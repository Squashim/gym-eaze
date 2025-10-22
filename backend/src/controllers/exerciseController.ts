import { Request, Response } from 'express';
import {
  createExercise,
  getExerciseByIdService,
  getExercisesByUserService,
  getExercisesService,
  getFavoriteExercisesService,
  toggleExerciseFavoriteService,
  toggleExerciseLikeService,
} from '../services/exerciseService';
import { validateExerciseFiles, ExerciseValidationError } from '../utils/exerciseValidation';
import * as exerciseService from '../services/exerciseService';
import { AuthRequest } from '../types/auth';
import { parseField } from '../utils/parsers';
import { ExerciseQuery, ExerciseRequest, PageSize } from '../types/exercise';

export const addExercise = async (req: Request, res: Response) => {
  const exerciseReq = req as ExerciseRequest;
  const authReq = req as AuthRequest;

  try {
    const { name, description, visibility, advancedLevel, muscleGroup, equipment, hashtags } =
      exerciseReq.body;

    const userId = authReq.userId;
    const imageFile = exerciseReq.files?.image?.[0];
    const videoFile = exerciseReq.files?.video?.[0];

    validateExerciseFiles(imageFile, videoFile);

    const trimmedName = name.trim();
    const trimmedDescription = description.trim();
    const muscleGroupArray = parseField(muscleGroup);
    const equipmentArray = parseField(equipment);
    const hashtagsArray = parseField(hashtags);

    const exercise = await createExercise(
      {
        author: userId!,
        name: trimmedName,
        description: trimmedDescription,
        visibility,
        advancedLevel,
        muscleGroup: muscleGroupArray,
        equipment: equipmentArray,
        hashtags: hashtagsArray,
      },
      imageFile!.buffer,
      videoFile!.buffer,
    );

    return res.status(201).json(exercise);
  } catch (error) {
    if (error instanceof ExerciseValidationError) {
      return res.status(400).json({ message: error.message });
    }

    return res.status(500).json({
      message: 'Błąd dodawania ćwiczenia',
    });
  }
};

export const deleteExercise = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;
    const deleted = await exerciseService.deleteExercise(id, userId!);

    if (!deleted) {
      return res.status(403).json({ message: 'Nie masz uprawnień do usunięcia tego ćwiczenia' });
    }

    res.status(200).json({ message: 'Ćwiczenie zostało usunięte' });
  } catch (err) {
    if (err instanceof Error) {
      return res.status(404).json({ message: err.message });
    }
    res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const updateExercise = async (req: AuthRequest, res: Response) => {
  const exerciseReq = req as ExerciseRequest;
  try {
    const { id } = req.params;
    const userId = req.userId;

    const { name, description, visibility, advancedLevel, muscleGroup, equipment, hashtags } =
      req.body;

    const imageFile = exerciseReq.files?.image?.[0];
    const videoFile = exerciseReq.files?.video?.[0];

    const trimmedName = name?.trim() || undefined;
    const trimmedDescription = description?.trim() || undefined;
    const muscleGroupArray = parseField(muscleGroup);
    const equipmentArray = parseField(equipment);
    const hashtagsArray = parseField(hashtags);

    const updated = await exerciseService.updateExercise(
      id,
      userId!,
      {
        name: trimmedName,
        description: trimmedDescription,
        visibility,
        advancedLevel,
        muscleGroup: muscleGroupArray.length ? muscleGroupArray : undefined,
        equipment: equipmentArray.length ? equipmentArray : undefined,
        hashtags: hashtagsArray.length ? hashtagsArray : undefined,
      },
      imageFile?.buffer,
      videoFile?.buffer,
    );

    if (!updated) {
      return res.status(403).json({ message: 'Nie masz uprawnień do edycji tego ćwiczenia' });
    }

    res.status(200).json(updated);
  } catch (error) {
    if (error instanceof ExerciseValidationError) {
      return res.status(400).json({ message: error.message });
    }
    if (error instanceof Error && error.message === 'NOT_FOUND') {
      return res.status(404).json({ message: 'Ćwiczenie nie znalezione' });
    }
    res.status(500).json({ message: 'Błąd edytowania ćwiczenia' });
  }
};

export const toggleExerciseLike = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: exerciseId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleExerciseLikeService(userId, exerciseId);

    res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const toggleExerciseFavorite = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: exerciseId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleExerciseFavoriteService(userId, exerciseId);

    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getExerciseById = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const exercise = await getExerciseByIdService(id, userId);

    if (!exercise) {
      return res.status(404).json({ message: 'Ćwiczenie nie znalezione' });
    }

    if (exercise === 'forbidden') {
      return res.status(403).json({ message: 'Brak dostępu do tego ćwiczenia' });
    }

    return res.json(exercise);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getExercisesByIds = async (req: AuthRequest, res: Response) => {
  try {
    const { ids } = req.body;
    const exercises = await exerciseService.getExercisesByIdsService(ids);
    return res.json(exercises);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getExercises = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    const { filters = {}, sortBy = 'latest', page = 1, pageSize = 9 }: ExerciseQuery = req.body;
    const exercises = await getExercisesService(filters, sortBy, page, pageSize, userId);

    return res.json(exercises);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getExercisesByUser = async (req: AuthRequest, res: Response) => {
  try {
    const { userId: targetUserId } = req.params;
    const currentUserId = req.userId;
    const includePrivate =
      typeof req.query.includePrivate === 'undefined' ? true : req.query.includePrivate === 'true';
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? (Number(req.query.pageSize) as PageSize) : 9;

    const exercises = await getExercisesByUserService(
      targetUserId,
      page,
      pageSize,
      includePrivate,
      currentUserId,
    );
    return res.json(exercises);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getFavoriteExercises = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? (Number(req.query.pageSize) as PageSize) : 9;

    const exercises = await getFavoriteExercisesService(userId, page, pageSize);
    return res.json(exercises);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(500).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
