export interface ExerciseValidationData {
  name?: string;
  description?: string;
  equipment?: string[];
  hashtags?: string[];
  muscleGroup?: string[];
}

export class ExerciseValidationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'ExerciseValidationError';
  }
}

export const validateExerciseData = (data: ExerciseValidationData): void => {

  if (!data.name || typeof data.name !== 'string' || data.name.trim().length === 0) {
    throw new ExerciseValidationError("Nazwa jest wymagana");
  }

  if (!data.description || typeof data.description !== 'string' || data.description.trim().length === 0) {
    throw new ExerciseValidationError("Opis jest wymagany");
  }

  const trimmedName = data.name.trim();
  const trimmedDescription = data.description.trim();

  if (trimmedName.length > 150) {
    throw new ExerciseValidationError("Nazwa może mieć maksymalnie 150 znaków");
  }
  
  if (trimmedDescription.length > 850) {
    throw new ExerciseValidationError("Opis może mieć maksymalnie 850 znaków");
  }
  
  if (data.equipment && data.equipment.length > 8) {
    throw new ExerciseValidationError("Możesz wybrać maksymalnie 8 sprzętów");
  }
  
  if (data.hashtags && data.hashtags.length > 50) {
    throw new ExerciseValidationError("Możesz napisać maksymalnie 50 hashtagów");
  }
};

export const validateExerciseFiles = (imageFile: any, videoFile: any): void => {
  if (!imageFile) {
    throw new ExerciseValidationError("Zdjęcie jest wymagane");
  }
  
  if (!videoFile) {
    throw new ExerciseValidationError("Filmik jest wymagany");
  }
};

export const validateExercisePatchData = (data: Partial<ExerciseValidationData>): void => {
  // walidacja nazwy
  if (data.name !== undefined) {
    if (typeof data.name !== 'string' || data.name.trim().length === 0) {
      throw new ExerciseValidationError("Nazwa nie może być pusta");
    }
    if (data.name.trim().length > 150) {
      throw new ExerciseValidationError("Nazwa może mieć maksymalnie 150 znaków");
    }
  }

  // walidacja opisu
  if (data.description !== undefined) {
    if (typeof data.description !== 'string' || data.description.trim().length === 0) {
      throw new ExerciseValidationError("Opis nie może być pusty");
    }
    if (data.description.trim().length > 850) {
      throw new ExerciseValidationError("Opis może mieć maksymalnie 850 znaków");
    }
  }

  // walidacja muscleGroup
  if (data.muscleGroup !== undefined) {
    if (!Array.isArray(data.muscleGroup) || data.muscleGroup.length === 0) {
      throw new ExerciseValidationError("MuscleGroup musi zawierać co najmniej jedną grupę mięśniową");
    }
  }

  // walidacja equipment
  if (data.equipment !== undefined) {
    if (!Array.isArray(data.equipment) || data.equipment.length === 0) {
      throw new ExerciseValidationError("Equipment musi zawierać co najmniej jeden sprzęt");
    }
    if (data.equipment.length > 8) {
      throw new ExerciseValidationError("Możesz wybrać maksymalnie 8 sprzętów");
    }
  }

  // walidacja hashtags
  if (data.hashtags !== undefined && data.hashtags.length > 50) {
    throw new ExerciseValidationError("Możesz napisać maksymalnie 50 hashtagów");
  }
};

