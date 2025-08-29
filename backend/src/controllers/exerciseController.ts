import { Request, Response } from "express";
import { createExercise, getExerciseByIdService, getExercisesByUserService, getExercisesService, getFavoriteExercisesService, toggleExerciseFavoriteService, toggleExerciseLikeService} from "../services/exerciseService";
import { validateExerciseFiles, ExerciseValidationError} from "../utils/exerciseValidation";
import * as exerciseService from "../services/exerciseService";
import { AuthRequest } from "../types/auth";

export const addExercise = async (req: Request, res: Response) => {
  try {
    const { 
      author, 
      name, 
      description, 
      visibility, 
      advancedLevel, 
      muscleGroup, 
      equipment, 
      hashtags 
    } = req.body;

    const imageFile = (req.files as any)?.image?.[0];
    const videoFile = (req.files as any)?.video?.[0];

    validateExerciseFiles(imageFile, videoFile);

    const exercise = await createExercise(
      {
        author,
        name,
        description,
        visibility,
        advancedLevel,
        muscleGroup,
        equipment,
        hashtags,
      },
      imageFile.buffer,
      videoFile.buffer
    );

    return res.status(201).json(exercise);
  } catch (error: any) {
    if (error instanceof ExerciseValidationError) {
      return res.status(400).json({ message: error.message });
    }

    return res.status(500).json({ 
      message: "Błąd dodawania ćwiczenia", 
      error: error.message 
    });
  }
};

export const deleteExercise = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;
    const deleted = await exerciseService.deleteExercise(id, userId!);

    if (!deleted) {
      return res.status(403).json({ message: "Nie masz uprawnień do usunięcia tego ćwiczenia" });
    }

    res.status(200).json({ message: "Ćwiczenie zostało usunięte" });
  } catch (err: any) {
    console.error(err);
    if (err.message === "NOT_FOUND") {
      return res.status(404).json({ message: "Ćwiczenie nie znalezione" });
    }
    res.status(500).json({ message: "Błąd serwera" });
  }
};

export const updateExercise = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const { 
      name, 
      description, 
      visibility, 
      advancedLevel, 
      muscleGroup, 
      equipment, 
      hashtags 
    } = req.body;

    const imageFile = (req.files as any)?.image?.[0];
    const videoFile = (req.files as any)?.video?.[0];

    const updated = await exerciseService.updateExercise(
      id,
      userId!,
      { 
        name, 
        description, 
        visibility, 
        advancedLevel, 
        muscleGroup, 
        equipment, 
        hashtags 
      },
      imageFile?.buffer,
      videoFile?.buffer
    );

    if (!updated) {
      return res.status(403).json({ message: "Nie masz uprawnień do edycji tego ćwiczenia" });
    }

    res.status(200).json(updated);
  } catch (error: any) {
    if (error instanceof ExerciseValidationError) {
      return res.status(400).json({ message: error.message });
    }
    if (error.message === "NOT_FOUND") {
      return res.status(404).json({ message: "Ćwiczenie nie znalezione" });
    }
    res.status(500).json({ message: "Błąd edytowania ćwiczenia", error: error.message });
  }
};

export const toggleExerciseLike = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId; 
    const { id: exerciseId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const result = await toggleExerciseLikeService(userId, exerciseId);

    res.status(200).json(result);
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};


export const toggleExerciseFavorite = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: exerciseId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const result = await toggleExerciseFavoriteService(userId, exerciseId);

    return res.json(result);

  } catch (err: any) {
    return res.status(400).json({ message: err.message });
  }
};


export const getExerciseById = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId; 

    const exercise = await getExerciseByIdService(id, userId);

    if (!exercise) {
      return res.status(404).json({ message: "Ćwiczenie nie znalezione" });
    }

    if (exercise === "forbidden") {
      return res.status(403).json({ message: "Brak dostępu do tego ćwiczenia" });
    }

    return res.json(exercise);
  } catch (err: any) {
    return res.status(500).json({ message: err.message });
  }
};

export const getExercises = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { sortBy = "latest", page = "1", pageSize = "10", ...filters } = req.query;

    const exercises = await getExercisesService(
      filters,
      sortBy as string,
      Number(page),
      Number(pageSize),
      userId
    );

    return res.json(exercises);
  } catch (err: any) {
    return res.status(500).json({ message: err.message });
  }
};

export const getExercisesByUser = async (req: AuthRequest, res: Response) => {
  try {
    const { userId: targetUserId } = req.params;
    const currentUserId = req.userId;

    const exercises = await getExercisesByUserService(targetUserId, currentUserId);
    return res.json(exercises);
  } catch (err: any) {
    return res.status(500).json({ message: err.message });
  }
};

export const getFavoriteExercises = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const exercises = await getFavoriteExercisesService(userId);
    return res.json(exercises);
  } catch (err: any) {
    return res.status(500).json({ message: err.message });
  }
};
