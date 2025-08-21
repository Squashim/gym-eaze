import { Request, Response } from "express";
import { createExercise } from "../services/exerciseService";
import { validateExerciseFiles, ExerciseValidationError } from "../utils/exerciseValidation";

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