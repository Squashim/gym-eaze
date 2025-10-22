import { Request, Response } from 'express';
import { AuthRequest } from '../types/auth';
import { PageSize, TrainingPlanQuery, TrainingPlanRequest } from '../types/trainingPlan';
import { TrainingPlanValidationError } from '../utils/trainingPlanValidation';
import {
  createTrainingPlan,
  generateTrainingPlanPDFService,
  getFavoriteTrainingPlansService,
  getTrainingPlanByIdService,
  getTrainingPlansByUserService,
  getTrainingPlansService,
  toggleTrainingPlanFavoriteService,
  toggleTrainingPlanLikeService,
  updateTrainingPlanService,
} from '../services/trainingPlanService';
import { parseField } from '../utils/parsers';
import TrainingPlan from '../models/trainingPlanModel';
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
    if (error instanceof TrainingPlanValidationError || error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }

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
    if (err instanceof Error) {
      return res.status(404).json({ message: err.message });
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
    const { name, description, goal, visibility, advancedLevel, hashtags } = trainingReq.body;
    let { days } = trainingReq.body;

    const imageFile = trainingReq.files?.image?.[0];

    const existingPlan = await TrainingPlan.findById(id);
    if (!existingPlan) {
      return res.status(404).json({ message: 'Plan treningowy nie został znaleziony' });
    }

    if (existingPlan.author.toString() !== userId) {
      return res.status(403).json({ message: 'Brak uprawnień do edycji tego planu' });
    }

    if (days && typeof days === 'string') {
      try {
        days = JSON.parse(days);
      } catch {
        return res.status(400).json({ message: 'Nieprawidłowy format dni' });
      }
    }

    const trimmedName = name?.trim() || undefined;
    const trimmedDescription = description?.trim() || undefined;
    const hashtagsArray = parseField(hashtags);

    const updated = await updateTrainingPlanService(
      existingPlan,
      {
        name: trimmedName,
        description: trimmedDescription,
        goal,
        visibility,
        advancedLevel,
        hashtags: hashtagsArray.length ? hashtagsArray : undefined,
        days,
      },
      imageFile?.buffer,
    );

    return res.status(200).json(updated);
  } catch (error) {
    if (error instanceof TrainingPlanValidationError || error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }

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
    const includePrivate =
      typeof req.query.includePrivate === 'undefined' ? true : req.query.includePrivate === 'true';
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? (Number(req.query.pageSize) as PageSize) : 9;

    const plans = await getTrainingPlansByUserService(
      targetUserId,
      page,
      pageSize,
      includePrivate,
      currentUserId,
    );
    return res.json(plans);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const downloadTrainingPlanPDF = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const pdfBuffer = await generateTrainingPlanPDFService(id, userId);

    res.setHeader('Content-Type', 'application/pdf');
    res.setHeader('Content-Disposition', `attachment; filename="plan-treningowy-${id}.pdf"`);
    res.send(pdfBuffer);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'Plan treningowy nie został znaleziony') {
        return res.status(404).json({ message: error.message });
      }
      if (error.message === 'Plan treningowy jest prywatny') {
        return res.status(403).json({ message: error.message });
      }
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
