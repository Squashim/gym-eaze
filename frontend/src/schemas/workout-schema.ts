import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Goal } from '@/constants/enums/goals';
import { Visibility } from '@/constants/enums/visibility';
import z from 'zod/v3';
import { WORKOUT } from '@/constants/workout';
import imageValidator from './validators/image-validator';
import { parseSecondsToTimeString } from '@/lib/parsers';

const repsSetValidator = z.object({
  id: z.string(),
  isRepsSet: z.literal(true),
  restAfterSet: z
    .number()
    .min(
      WORKOUT.DEFAULT_REST_AFTER_SET,
      `Odpoczynek po serii musi wynosić co najmniej ${WORKOUT.DEFAULT_REST_AFTER_SET} sekund.`,
    )
    .max(
      WORKOUT.MAX_REST_AFTER_SET,
      `Odpoczynek po serii nie może przekraczać ${parseSecondsToTimeString(WORKOUT.MAX_REST_AFTER_SET)}.`,
    ),
  sets: z
    .array(
      z.object({
        repetitions: z
          .number()
          .min(1, 'Liczba powtórzeń musi być dodatnia.')
          .max(
            WORKOUT.MAX_REPS_PER_SET,
            `Liczba powtórzeń nie może przekraczać ${WORKOUT.MAX_REPS_PER_SET}.`,
          ),
        weight: z
          .number()
          .max(WORKOUT.MAX_WEIGHT, `Maksymalna waga to ${WORKOUT.MAX_WEIGHT} kg.`)
          .nullable()
          .optional(),
      }),
    )
    .min(1, 'Ćwiczenie musi mieć przynajmniej jedną serię.')
    .max(
      WORKOUT.MAX_SETS_PER_EXERCISE,
      `Ćwiczenie może mieć maksymalnie ${WORKOUT.MAX_SETS_PER_EXERCISE} serii.`,
    ),
});

const durationSetValidator = z.object({
  id: z.string(),
  isRepsSet: z.literal(false),
  restAfterSet: z
    .number()
    .min(
      WORKOUT.DEFAULT_REST_AFTER_SET,
      `Odpoczynek po serii musi wynosić co najmniej ${WORKOUT.DEFAULT_REST_AFTER_SET} sekund.`,
    )
    .max(
      WORKOUT.MAX_REST_AFTER_SET,
      `Odpoczynek po serii nie może przekraczać ${parseSecondsToTimeString(WORKOUT.MAX_REST_AFTER_SET)} minut.`,
    ),
  sets: z
    .array(
      z.object({
        duration: z
          .number()
          .min(
            WORKOUT.DEFAULT_SETS_DURATION,
            `Czas trwania ćwiczenie musi wynosić co najmniej ${WORKOUT.DEFAULT_SETS_DURATION} sekund.`,
          )
          .max(
            WORKOUT.MAX_DURATION_PER_SET,
            `Czas trwania ćwiczenia nie może przekraczać ${parseSecondsToTimeString(WORKOUT.MAX_REST_AFTER_SET)}.`,
          ),
      }),
    )
    .min(1, 'Ćwiczenie musi mieć przynajmniej jedną serię.')
    .max(
      WORKOUT.MAX_SETS_PER_EXERCISE,
      `Ćwiczenie może mieć maksymalnie ${WORKOUT.MAX_SETS_PER_EXERCISE} serii.`,
    ),
});

const dayExercisesValidator = z.discriminatedUnion('isRepsSet', [
  repsSetValidator,
  durationSetValidator,
]);

const trainingDaySchema = z
  .object({
    name: z
      .string()
      .min(1, 'Nazwa dnia treningowego jest wymagana.')
      .max(
        WORKOUT.MAX_DAY_NAME_LENGTH,
        `Nazwa dnia treningowego nie może być dłuższa niż ${WORKOUT.MAX_DAY_NAME_LENGTH} znaków.`,
      ),
    dayNumber: z.number(),
    isRestDay: z.boolean(),
    exercises: z.array(dayExercisesValidator),
  })
  .superRefine((day, ctx) => {
    if (
      !day.isRestDay &&
      (day.exercises.length < 1 || day.exercises.length > WORKOUT.MAX_EXERCISES_PER_DAY)
    ) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['exercises'],
        message: `Dzień treningowy musi zawierać od 1 do ${WORKOUT.MAX_EXERCISES_PER_DAY} ćwiczeń.`,
      });
    }
  });

const workoutStepOneSchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa planu treningowego jest wymagana.')
    .max(
      WORKOUT.MAX_WORKOUT_NAME_LENGTH,
      `Nazwa planu treningowego nie może być dłuższa niż ${WORKOUT.MAX_WORKOUT_NAME_LENGTH} znaków.`,
    ),
  image: imageValidator,
  advancedLevel: z.nativeEnum(AdvancedLevel),
  visibility: z.nativeEnum(Visibility),
  goal: z.nativeEnum(Goal),
  description: z
    .string()
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= WORKOUT.MAX_DESCRIPTION_LENGTH;
      },
      {
        message: `Opis planu treningowego nie może być dłuższy niż ${WORKOUT.MAX_DESCRIPTION_LENGTH} znaków.`,
      },
    )
    .optional(),
  hashtags: z
    .array(z.string())
    .max(WORKOUT.MAX_HASHTAGS, `Możesz dodać maksymalnie ${WORKOUT.MAX_HASHTAGS} hashtagów.`),
});

const workoutStepTwoSchema = z.object({
  days: z
    .array(trainingDaySchema)
    .min(1, 'Plan musi zawierać przynajmniej jeden dzień treningowy.')
    .max(
      WORKOUT.MAX_DAYS,
      `Plan nie może zawierać więcej niż ${WORKOUT.MAX_DAYS} dni treningowych.`,
    )
    .refine((days) => days.some((day) => !day.isRestDay), {
      message: 'Plan musi zawierać przynajmniej jeden dzień, który nie jest dniem wolnym.',
    }),
});

export const workoutSchema = z.object({
  stepOne: workoutStepOneSchema,
  stepTwo: workoutStepTwoSchema,
});

export type WorkoutSchema = z.infer<typeof workoutSchema>;
export type TrainingDaySchema = z.infer<typeof trainingDaySchema>;
