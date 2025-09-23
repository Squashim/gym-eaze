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

const repsSetValidator = z.object({
  id: z.string(),
  isRepsSet: z.literal(true),
  restAfterSet: z
    .number()
    .min(30, 'Odpoczynek po serii musi wynosić co najmniej 30 sekund.')
    .max(5400, 'Odpoczynek po serii nie może przekraczać 90 minut.'),
  sets: z
    .array(
      z.object({
        repetitions: z
          .number()
          .min(1, 'Liczba powtórzeń musi być większa od 0.')
          .max(100, 'Liczba powtórzeń nie może przekraczać 100.'),
        weight: z.number().max(300, 'Maksymalna waga to 300 kg.').nullable().optional(),
      }),
    )
    .min(1, 'Ćwiczenie musi mieć przynajmniej jedną serię.')
    .max(15, 'Ćwiczenie może mieć maksymalnie 15 serii.'),
});

const durationSetValidator = z.object({
  id: z.string(),
  isRepsSet: z.literal(false),
  restAfterSet: z
    .number()
    .min(30, 'Odpoczynek po serii musi wynosić co najmniej 30 sekund.')
    .max(5400, 'Odpoczynek po serii nie może przekraczać 90 minut.'),
  sets: z
    .array(
      z.object({
        duration: z
          .number()
          .min(30, 'Czas trwania ćwiczenie musi wynosić co najmniej 1 minutę.')
          .max(21600, 'Czas trwania ćwiczenia nie może przekraczać 6 godzin.'),
      }),
    )
    .min(1, 'Ćwiczenie musi mieć przynajmniej jedną serię.')
    .max(15, 'Ćwiczenie może mieć maksymalnie 15 serii.'),
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
      .max(100, 'Nazwa dnia treningowego nie może być dłuższa niż 100 znaków.'),
    dayNumber: z.number(),
    isRestDay: z.boolean(),
    exercises: z.array(dayExercisesValidator),
  })
  .superRefine((day, ctx) => {
    if (!day.isRestDay && (day.exercises.length < 1 || day.exercises.length > 15)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['exercises'],
        message: 'Dzień treningowy musi zawierać od 1 do 15 ćwiczeń.',
      });
    }
  });

const workoutStepOneSchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa planu treningowego jest wymagana.')
    .max(150, 'Nazwa planu treningowego nie może być dłuższa niż 150 znaków.'),
  image: imageValidator,
  advancedLevel: z.nativeEnum(AdvancedLevel),
  visibility: z.nativeEnum(Visibility),
  goal: z.nativeEnum(Goal),
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
    .max(30, 'Plan nie może zawierać więcej niż 30 dni treningowych.')
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
