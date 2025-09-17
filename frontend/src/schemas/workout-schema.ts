import { AdvancedLevel } from '@/constants/advanced-level';
import { Goal } from '@/constants/goals';
import { Visibility } from '@/constants/visibility';
import z from 'zod/v3';

const IMAGE_MIME_TYPES = ['image/png', 'image/jpg', 'image/webp'];
const IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB

const MAX_DESCRIPTION_LENGTH = 850;

const imageValidator = z
  .instanceof(File, { message: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= IMAGE_MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => IMAGE_MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  );

const exerciseSetValidator = z.object({
  repetitions: z.number(),
  weight: z.number().optional(),
  restAfterSet: z.number().optional(),
});

const dayExercisesValidator = z.object({
  id: z.string(),
  isRepsSet: z.boolean(),
  duration: z.number(),
  sets: z.array(exerciseSetValidator),
});

const trainingDaySchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa dnia treningowego jest wymagana.')
    .max(100, 'Nazwa dnia treningowego nie może być dłuższa niż 100 znaków.'),
  isRestDay: z.boolean(),
  exercises: z.array(dayExercisesValidator),
});

const workoutStepOneSchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa planu treningowego jest wymagana.')
    .max(150, 'Nazwa planu treningowego nie może być dłuższa niż 150 znaków.'),
  image: imageValidator,
  advancedLevel: z.nativeEnum(AdvancedLevel),
  visibility: z.nativeEnum(Visibility),
  trainingGoal: z.nativeEnum(Goal),
  description: z
    .string()
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= MAX_DESCRIPTION_LENGTH;
      },
      {
        message: 'Opis planu treningowego nie może być dłuższy niż 850 znaków.',
      },
    )
    .optional(),
  hashtags: z.array(z.string()).max(50, 'Możesz dodać maksymalnie 50 hashtagów.'),
});

const workoutStepTwoSchema = z.object({
  days: z
    .array(trainingDaySchema)
    .min(1, 'Plan musi zawierać przynajmniej jeden dzień treningowy.')
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
