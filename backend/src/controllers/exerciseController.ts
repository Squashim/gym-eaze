import { Request, Response } from "express";
import { createExercise } from "../services/exerciseService";
import { validateExerciseFiles, ExerciseValidationError } from "../utils/exerciseValidation";
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