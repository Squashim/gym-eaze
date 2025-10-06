import { CreateTrainingPlanData, DayExercise } from '../types/trainingPlan';
import { parseDescription } from './parsers';

export class TrainingPlanValidationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'TrainingPlanValidationError';
  }
}

export const validateTrainingPlanData = (data: CreateTrainingPlanData) => {
  if (!data.name || data.name.trim().length === 0) {
    throw new TrainingPlanValidationError('Tytuł jest wymagany');
  }
  if (data.name.length > 150) {
    throw new TrainingPlanValidationError('Tytuł może mieć maksymalnie 150 znaków');
  }

  if (data.description && parseDescription(data.description).length > 850) {
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

      if (day.exercises.length > 15) {
        throw new TrainingPlanValidationError(
          `Dzień treningowy "${day.name}" może zawierać maksymalnie 15 ćwiczeń`,
        );
      }

      for (const exercise of day.exercises) {
        if (!exercise.id) {
          throw new TrainingPlanValidationError('Każde ćwiczenie musi mieć przypisane ID');
        }

        if (exercise.sets.length === 0) {
          throw new TrainingPlanValidationError('Ćwiczenie musi mieć co najmniej jedną serię');
        }

        if (exercise.sets.length > 15) {
          throw new TrainingPlanValidationError('Ćwiczenie może mieć maksymalnie 15 serii');
        }

        if (typeof exercise.restAfterSet !== 'number' || exercise.restAfterSet < 30) {
          throw new TrainingPlanValidationError('Minimalny odpoczynek po serii to 30 sekund');
        }

        if (exercise.restAfterSet > 5400) {
          throw new TrainingPlanValidationError('Maksymalny odpoczynek po serii to 1h 30min');
        }

        if (exercise.isRepsSet) {
          for (const set of exercise.sets) {
            if (set.repetitions < 1) {
              throw new TrainingPlanValidationError(
                'Liczba powtórzeń w serii musi być większa od 0',
              );
            }

            if (set.repetitions > 100) {
              throw new TrainingPlanValidationError('Maksymalna liczba powtórzeń w serii to 100');
            }

            if (set.weight && set.weight < 0) {
              throw new TrainingPlanValidationError('Ciężar w serii nie może być ujemny');
            }

            if (set.weight && set.weight > 300) {
              throw new TrainingPlanValidationError('Maksymalny ciężar w serii to 300kg');
            }
          }
        } else {
          for (const set of exercise.sets) {
            if (set.duration < 30) {
              throw new TrainingPlanValidationError(
                'Czas trwania serii musi być co najmniej 30 sekund',
              );
            }

            if (set.duration > 21600) {
              throw new TrainingPlanValidationError('Maksymalny czas trwania serii to 6h');
            }
          }
        }
      }
    }
  }
};

export const validateTrainingPlanUpdateData = (data: Partial<CreateTrainingPlanData>) => {
  if (data.name !== undefined) {
    if (typeof data.name !== 'string' || data.name.length === 0) {
      throw new TrainingPlanValidationError('Tytuł jest wymagany');
    }
    if (data.name.length > 150) {
      throw new TrainingPlanValidationError('Tytuł może mieć maksymalnie 150 znaków');
    }
  }

  if (data.description !== undefined) {
    if (data.description && parseDescription(data.description).length > 850) {
      throw new TrainingPlanValidationError('Opis może mieć maksymalnie 850 znaków');
    }
  }

  if (data.hashtags !== undefined && data.hashtags.length > 50) {
    throw new TrainingPlanValidationError('Możesz dodać maksymalnie 50 hashtagów');
  }

  if (data.days !== undefined) {
    if (!data.days || data.days.length === 0) {
      throw new TrainingPlanValidationError(
        'Plan musi zawierać co najmniej jeden dzień treningowy',
      );
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

        if (day.exercises.length > 15) {
          throw new TrainingPlanValidationError(
            `Dzień treningowy "${day.name}" może zawierać maksymalnie 15 ćwiczeń`,
          );
        }

        for (const exercise of day.exercises) {
          if (!exercise.id) {
            throw new TrainingPlanValidationError('Każde ćwiczenie musi mieć przypisane ID');
          }

          if (exercise.sets.length === 0) {
            throw new TrainingPlanValidationError('Ćwiczenie musi mieć co najmniej jedną serię');
          }

          if (exercise.sets.length > 15) {
            throw new TrainingPlanValidationError('Ćwiczenie może mieć maksymalnie 15 serii');
          }

          if (typeof exercise.restAfterSet !== 'number' || exercise.restAfterSet < 30) {
            throw new TrainingPlanValidationError('Minimalny odpoczynek po serii to 30 sekund');
          }

          if (exercise.restAfterSet > 5400) {
            throw new TrainingPlanValidationError('Maksymalny odpoczynek po serii to 1h 30min');
          }

          if (exercise.isRepsSet) {
            for (const set of exercise.sets) {
              if (set.repetitions < 1) {
                throw new TrainingPlanValidationError(
                  'Liczba powtórzeń w serii musi być większa od 0',
                );
              }

              if (set.repetitions > 100) {
                throw new TrainingPlanValidationError('Maksymalna liczba powtórzeń w serii to 100');
              }

              if (set.weight && set.weight < 0) {
                throw new TrainingPlanValidationError('Ciężar w serii nie może być ujemny');
              }

              if (set.weight && set.weight > 300) {
                throw new TrainingPlanValidationError('Maksymalny ciężar w serii to 300kg');
              }
            }
          } else {
            for (const set of exercise.sets) {
              if (set.duration < 30) {
                throw new TrainingPlanValidationError(
                  'Czas trwania serii musi być co najmniej 30 sekund',
                );
              }

              if (set.duration > 21600) {
                throw new TrainingPlanValidationError('Maksymalny czas trwania serii to 6h');
              }
            }
          }
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

export const calculateExerciseDuration = (
  exercise: DayExercise,
  includeLastRest: boolean = true,
): number => {
  const AVERAGE_REP_TIME = 3;
  const restAfterSetCount = includeLastRest
    ? exercise.sets.length
    : Math.max(0, exercise.sets.length - 1);
  const totalRestTime = restAfterSetCount * exercise.restAfterSet;

  if (!exercise.sets || exercise.sets.length === 0) {
    return 0;
  }

  if (exercise.isRepsSet) {
    const totalRepsExerciseTime = exercise.sets.reduce((setTime, set) => {
      return setTime + set.repetitions * AVERAGE_REP_TIME;
    }, 0);

    return totalRepsExerciseTime + totalRestTime;
  } else {
    const totalDurationExerciseTime = exercise.sets.reduce((setTime, set) => {
      return setTime + set.duration;
    }, 0);

    return totalDurationExerciseTime + totalRestTime;
  }
};
