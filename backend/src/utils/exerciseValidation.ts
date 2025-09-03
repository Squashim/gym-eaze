import { CreateExerciseData, UpdateExerciseData } from '../types/exercise';
import { parseDescription } from './parsers';

export class ExerciseValidationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'ExerciseValidationError';
  }
}

export const validateExerciseData = (data: CreateExerciseData): void => {
  if (!data.name || typeof data.name !== 'string' || data.name.length === 0) {
    throw new ExerciseValidationError('Nazwa jest wymagana');
  }

  if (!data.description || typeof data.description !== 'string' || data.description.length === 0) {
    throw new ExerciseValidationError('Opis jest wymagany');
  }

  if (data.name.length > 150) {
    throw new ExerciseValidationError('Nazwa może mieć maksymalnie 150 znaków');
  }

  if (parseDescription(data.description).length > 850) {
    throw new ExerciseValidationError('Opis może mieć maksymalnie 850 znaków');
  }

  if (!data.muscleGroup || !Array.isArray(data.muscleGroup) || data.muscleGroup.length === 0) {
    throw new ExerciseValidationError('Wybierz przynajmniej jedną grupę mięśniową');
  }

  if (data.muscleGroup.length > 8) {
    throw new ExerciseValidationError('Możesz wybrać maksymalnie 8 grup mięśniowych');
  }

  if (!data.equipment || !Array.isArray(data.equipment) || data.equipment.length === 0) {
    throw new ExerciseValidationError('Wybierz przynajmniej jeden sprzęt');
  }

  if (data.equipment.length > 8) {
    throw new ExerciseValidationError('Możesz wybrać maksymalnie 8 sprzętów');
  }

  if (data.hashtags && data.hashtags.length > 50) {
    throw new ExerciseValidationError('Możesz dodać maksymalnie 50 hashtagów');
  }
};

export const validateExerciseFiles = (
  imageFile: Express.Multer.File | undefined,
  videoFile: Express.Multer.File | undefined,
): void => {
  if (!imageFile) {
    throw new ExerciseValidationError('Zdjęcie jest wymagane');
  }

  if (!videoFile) {
    throw new ExerciseValidationError('Filmik jest wymagany');
  }
};

export const validateExercisePatchData = (data: UpdateExerciseData): void => {
  if (data.name !== undefined) {
    if (typeof data.name !== 'string' || data.name.trim().length === 0) {
      throw new ExerciseValidationError('Nazwa nie może być pusta');
    }
    if (data.name.trim().length > 150) {
      throw new ExerciseValidationError('Nazwa może mieć maksymalnie 150 znaków');
    }
  }

  if (data.description !== undefined) {
    if (typeof data.description !== 'string' || data.description.trim().length === 0) {
      throw new ExerciseValidationError('Opis nie może być pusty');
    }
    if (parseDescription(data.description).length > 850) {
      throw new ExerciseValidationError('Opis może mieć maksymalnie 850 znaków');
    }
  }

  if (data.muscleGroup !== undefined) {
    if (!Array.isArray(data.muscleGroup) || data.muscleGroup.length === 0) {
      throw new ExerciseValidationError('Wymagana co najmniej jedna grupa mięśniowa');
    }
    if (data.muscleGroup.length > 8) {
      throw new ExerciseValidationError('Możesz wybrać maksymalnie 8 grup mięśniowych');
    }
  }

  if (data.equipment !== undefined) {
    if (!Array.isArray(data.equipment) || data.equipment.length === 0) {
      throw new ExerciseValidationError('Wymagany co najmniej jeden sprzęt');
    }
    if (data.equipment.length > 8) {
      throw new ExerciseValidationError('Możesz wybrać maksymalnie 8 sprzętów');
    }
  }

  if (data.hashtags !== undefined && data.hashtags.length > 50) {
    throw new ExerciseValidationError('Możesz dodać maksymalnie 50 hashtagów');
  }
};
