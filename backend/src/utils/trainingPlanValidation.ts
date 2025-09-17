import { CreateTrainingPlanData, DayExercise, TrainingDay } from '../types/trainingPlan';

export class TrainingPlanValidationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'TrainingPlanValidationError';
  }
}

export const validateTrainingPlanData = (data: CreateTrainingPlanData) => {
  if (!data.title || data.title.trim().length === 0) {
    throw new TrainingPlanValidationError('Tytuł jest wymagany');
  }
  if (data.title.length > 150) {
    throw new TrainingPlanValidationError('Tytuł może mieć maksymalnie 150 znaków');
  }

  if (data.description && data.description.length > 850) {
    throw new TrainingPlanValidationError('Opis może mieć maksymalnie 850 znaków');
  }

  if (!data.goal) {
    throw new TrainingPlanValidationError('Cel jest wymagany');
  }

  if (!data.visibility) {
    throw new TrainingPlanValidationError('Widoczność jest wymagana');
  }

  if (!data.advancedLevel) {
    throw new TrainingPlanValidationError('Poziom zaawansowania jest wymagany');
  }

  if (data.hashtags && data.hashtags.length > 50) {
    throw new TrainingPlanValidationError('Możesz dodać maksymalnie 50 hashtagów');
  }

  if (!data.days || data.days.length === 0) {
    throw new TrainingPlanValidationError('Plan musi zawierać co najmniej jeden dzień treningowy');
  }

  if (data.days.length > 30) {
    throw new TrainingPlanValidationError('Plan może mieć maksymalnie 30 dni');
  }

  for (const day of data.days) {
    if (!day.name || day.name.trim().length === 0) {
      throw new TrainingPlanValidationError('Każdy dzień musi mieć nazwę');
    }

    if (day.name.length > 100) {
      throw new TrainingPlanValidationError('Nazwa dnia może mieć maksymalnie 100 znaków');
    }

    if (!day.isRestDay) {
      if (!day.exercises || day.exercises.length === 0) {
        throw new TrainingPlanValidationError(
          `Dzień treningowy "${day.name}" musi zawierać co najmniej jedno ćwiczenie`,
        );
      }

      for (const exercise of day.exercises) {
        if (!exercise.id) {
          throw new TrainingPlanValidationError('Każde ćwiczenie musi mieć przypisane ID');
        }

        if (!exercise.isRepsSet && !exercise.duration) {
          throw new TrainingPlanValidationError(
            'Ćwiczenie czasowe musi mieć określony czas trwania',
          );
        }

        if (exercise.isRepsSet) {
          if (!exercise.sets || exercise.sets.length === 0) {
            throw new TrainingPlanValidationError(
              'Ćwiczenie z powtórzeniami musi mieć co najmniej jedną serię',
            );
          }
        }

        if (typeof exercise.restAfterSet !== 'number' || exercise.restAfterSet < 0) {
          throw new TrainingPlanValidationError('RestAfterSet musi być liczbą większą lub równą 0');
        }
      }
    }
  }
};

export const validateTrainingPlanFile = (imageBuffer: Buffer): void => {
  if (!imageBuffer) {
    throw new TrainingPlanValidationError('Zdjęcie jest wymagane');
  }
};

export const calculateDayDuration = (day: TrainingDay, includeLastRest: boolean = true): number => {
  if (day.isRestDay) {
    return 0;
  }
  return day.exercises.reduce((totalTime, exercise) => {
    return totalTime + calculateExerciseDuration(exercise, includeLastRest);
  }, 0);
};

const calculateExerciseDuration = (
  exercise: DayExercise,
  includeLastRest: boolean = true,
): number => {
  const AVERAGE_REP_TIME = 3; //--------------------------------------------------------------------------------------------CZAS TRWANIA JEDNEGO POWTÓRZENIA W SEKUNDACH--------------------------------------------------------------------

  if (exercise.isRepsSet) {
    if (!exercise.sets || exercise.sets.length === 0) {
      return 0;
    }

    const totalExerciseTime = exercise.sets.reduce((setTime, set) => {
      return setTime + set.repetitions * AVERAGE_REP_TIME;
    }, 0);

    const restSetsCount = includeLastRest
      ? exercise.sets.length
      : Math.max(0, exercise.sets.length - 1);

    const totalRestTime = restSetsCount * exercise.restAfterSet;

    return totalExerciseTime + totalRestTime;
  } else {
    if (!exercise.duration) {
      return 0;
    }

    const numberOfSets = exercise.sets?.length || 1;
    const totalExerciseTime = exercise.duration * numberOfSets;

    const restSetsCount = includeLastRest ? numberOfSets : Math.max(0, numberOfSets - 1);

    const totalRestTime = restSetsCount * exercise.restAfterSet;

    return totalExerciseTime + totalRestTime;
  }
};
