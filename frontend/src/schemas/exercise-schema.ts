import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Visibility } from '@/constants/enums/visibility';
import z from 'zod/v3';
import { EXERCISE } from '@/constants/exercise';
import imageValidator from './validators/image-validator';
import videoValidator from './validators/video-validator';

const exerciseStepOneSchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa ćwiczenia jest wymagana.')
    .max(
      EXERCISE.MAX_NAME_LENGTH,
      `Nazwa ćwiczenia nie może być dłuższa niż ${EXERCISE.MAX_NAME_LENGTH} znaków.`,
    ),

  image: imageValidator,
  video: videoValidator,
  visibility: z.nativeEnum(Visibility),
  advancedLevel: z.nativeEnum(AdvancedLevel),
});

const exerciseStepTwoSchema = z.object({
  description: z
    .string()
    .min(1, 'Opis ćwiczenia jest wymagany.')
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= EXERCISE.MAX_DESCRIPTION_LENGTH;
      },
      {
        message: `Opis ćwiczenia nie może być dłuższy niż ${EXERCISE.MAX_DESCRIPTION_LENGTH} znaków.`,
      },
    ),
  muscleGroup: z
    .array(z.string())
    .min(1, 'Wybierz przynajmniej jedną grupę mięśniową.')
    .max(
      EXERCISE.MAX_MUSCLE_GROUPS,
      `Możesz wybrać maksymalnie ${EXERCISE.MAX_MUSCLE_GROUPS} grup mięśniowych.`,
    ),
  equipment: z
    .array(z.string())
    .min(1, 'Wybierz przynajmniej jeden sprzęt.')
    .max(EXERCISE.MAX_EQUIPMENT, `Możesz wybrać maksymalnie ${EXERCISE.MAX_EQUIPMENT} sprzętów.`),
  hashtags: z
    .array(z.string())
    .max(EXERCISE.MAX_HASHTAGS, `Możesz dodać maksymalnie ${EXERCISE.MAX_HASHTAGS} hashtagów.`),
});

export const exerciseSchema = z.object({
  stepOne: exerciseStepOneSchema,
  stepTwo: exerciseStepTwoSchema,
});

export type ExerciseSchema = z.infer<typeof exerciseSchema>;
