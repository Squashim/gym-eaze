import { Request, Response } from 'express';
import { AuthRequest } from '../types/auth';
import { PageSize, TrainingPlanQuery, TrainingPlanRequest } from '../types/trainingPlan';
import {
  calculateExerciseDuration,
  TrainingPlanValidationError,
  validateTrainingPlanData,
  validateTrainingPlanFile,
} from '../utils/trainingPlanValidation';
import {
  createTrainingPlan,
  getFavoriteTrainingPlansService,
  getTrainingPlanByIdService,
  getTrainingPlansByUserService,
  getTrainingPlansService,
  toggleTrainingPlanFavoriteService,
  toggleTrainingPlanLikeService,
} from '../services/trainingPlanService';
import { parseField } from '../utils/parsers';
import TrainingPlan from '../models/trainingPlanModel';
import { uploadTrainingPlanMedia } from '../config/cloudinary';
import { deleteTrainingPlan as deleteTrainingPlanService } from '../services/trainingPlanService';

export const addTrainingPlan = async (req: Request, res: Response) => {
  const trainingReq = req as TrainingPlanRequest;
  const authReq = req as AuthRequest;

  try {
    const { name, description, goal, visibility, advancedLevel, hashtags } = trainingReq.body;
    let { days } = trainingReq.body;
    const userId = authReq.userId;
    const imageFile = trainingReq.files?.image?.[0];

    if (!imageFile) {
      return res.status(400).json({ message: 'Zdjęcie jest wymagane' });
    }

    if (typeof days === 'string') {
      try {
        days = JSON.parse(days);
      } catch {
        return res.status(400).json({ message: 'Nieprawidłowy format dni' });
      }
    }

    const trimmedName = name.trim();
    const trimmedDescription = description && description.trim();
    const hashtagsArray = parseField(hashtags);

    const trainingPlan = await createTrainingPlan(
      {
        name: trimmedName,
        description: trimmedDescription,
        goal,
        visibility,
        advancedLevel,
        hashtags: hashtagsArray,
        days,
      },
      imageFile.buffer,
      userId!,
    );

    return res.status(201).json(trainingPlan);
  } catch (error) {
    if (error instanceof TrainingPlanValidationError) {
      return res.status(400).json({ message: error.message });
    }

    console.error('Error:', error);
    return res.status(500).json({
      message: 'Błąd podczas dodawania planu treningowego',
    });
  }
};

export const deleteTrainingPlan = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;
    const deleted = await deleteTrainingPlanService(id, userId!);

    if (!deleted) {
      return res
        .status(403)
        .json({ message: 'Nie masz uprawnień do usunięcia tego planu treningowego' });
    }

    res.status(200).json({ message: 'Plan treningowy został usunięty' });
  } catch (err) {
    if (err instanceof Error && err.message === 'NOT_FOUND') {
      return res.status(404).json({ message: 'Plan treningowy nie znaleziony' });
    }
    res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const updateTrainingPlan = async (req: Request, res: Response) => {
  const trainingReq = req as TrainingPlanRequest;
  const authReq = req as AuthRequest;

  try {
    const { id } = req.params;
    const userId = authReq.userId;
    const imageFile = trainingReq.files?.image?.[0];

    const existingPlan = await TrainingPlan.findById(id);
    if (!existingPlan) {
      return res.status(404).json({ message: 'Plan treningowy nie został znaleziony' });
    }

    if (existingPlan.author.toString() !== userId) {
      return res.status(403).json({ message: 'Brak uprawnień do edycji tego planu' });
    }

    const { name, description, goal, visibility, advancedLevel, hashtags } = trainingReq.body;
    let { days } = trainingReq.body;

    if (days && typeof days === 'string') {
      try {
        days = JSON.parse(days);
      } catch {
        return res.status(400).json({ message: 'Nieprawidłowy format dni' });
      }
    }

    const updateData: any = {};

    if (name !== undefined) updateData.name = name.trim();
    if (description !== undefined) updateData.description = description?.trim();
    if (goal !== undefined) updateData.goal = goal;
    if (visibility !== undefined) updateData.visibility = visibility;
    if (advancedLevel !== undefined) updateData.advancedLevel = advancedLevel;
    if (hashtags !== undefined) updateData.hashtags = parseField(hashtags);

    if (days) {
      const daysWithDuration = days.map((day: any) => {
        const exercisesWithDuration = day.exercises.map(
          (exercise: any, idx: number, arr: any[]) => {
            const isLastExercise = idx === arr.length - 1;
            const duration = calculateExerciseDuration(exercise, isLastExercise);
            return {
              ...exercise,
              duration,
            };
          },
        );

        const totalDuration = exercisesWithDuration.reduce(
          (sum: number, exercise: any) => sum + exercise.duration,
          0,
        );

        return {
          ...day,
          totalDuration,
          exercises: exercisesWithDuration,
        };
      });

      updateData.days = daysWithDuration;
    }

    if (imageFile) {
      validateTrainingPlanFile(imageFile.buffer);
      const { imageUrl } = await uploadTrainingPlanMedia(imageFile.buffer);
      updateData.image = imageUrl;
    }

    if (Object.keys(updateData).length > 0) {
      const dataToValidate = {
        name: updateData.name || existingPlan.name,
        goal: updateData.goal || existingPlan.goal,
        visibility: updateData.visibility || existingPlan.visibility,
        advancedLevel: updateData.advancedLevel || existingPlan.advancedLevel,
        days: updateData.days || existingPlan.days,
        hashtags: updateData.hashtags || existingPlan.hashtags,
      };

      validateTrainingPlanData(dataToValidate);
    }

    const updatedPlan = await TrainingPlan.findByIdAndUpdate(
      id,
      { $set: updateData },
      { new: true, runValidators: true },
    );

    return res.status(200).json(updatedPlan);
  } catch (error) {
    if (error instanceof TrainingPlanValidationError) {
      return res.status(400).json({ message: error.message });
    }

    console.error('Error:', error);
    return res.status(500).json({
      message: 'Błąd podczas aktualizacji planu treningowego',
    });
  }
};

export const getFavoriteTrainingPlans = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? (Number(req.query.pageSize) as PageSize) : 9;

    const plans = await getFavoriteTrainingPlansService(userId, page, pageSize);
    return res.json(plans);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const toggleTrainingPlanLike = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: planId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleTrainingPlanLikeService(userId, planId);

    res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const toggleTrainingPlanFavorite = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: planId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleTrainingPlanFavoriteService(userId, planId);

    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getTrainingPlanById = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const trainingPlan = await getTrainingPlanByIdService(id, userId);

    if (!trainingPlan) {
      return res.status(404).json({ message: 'Plan treningowy nie został znaleziony' });
    }

    return res.json(trainingPlan);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'Plan treningowy jest prywatny') {
        return res.status(403).json({ message: error.message });
      }
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getTrainingPlans = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    const { filters = {}, sortBy = 'latest', page = 1, pageSize = 9 }: TrainingPlanQuery = req.body;

    const trainingPlans = await getTrainingPlansService(filters, sortBy, page, pageSize, userId);

    return res.json(trainingPlans);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getTrainingPlansByUser = async (req: AuthRequest, res: Response) => {
  try {
    const { userId: targetUserId } = req.params;
    const currentUserId = req.userId;
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? (Number(req.query.pageSize) as PageSize) : 9;

    const plans = await getTrainingPlansByUserService(targetUserId, page, pageSize, currentUserId);
    return res.json(plans);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
