import { AdvancedLevel } from '@/constants/advanced-level';
import { Visibility } from '@/constants/visibility';
import z from 'zod/v3';

const IMAGE_MIME_TYPES = ['image/png', 'image/jpg', 'image/webp'];
const IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB

const VIDEO_MIME_TYPES = ['video/mp4', 'video/webm'];
const VIDEO_MAX_SIZE = 20 * 1024 * 1024; // 20MB

const MAX_DESCRIPTION_LENGTH = 850;

const imageValidator = z
  .instanceof(File, { message: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= IMAGE_MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => IMAGE_MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  );

const videoValidator = z
  .instanceof(File, { message: 'Filmik jest wymagany.' })
  .refine((file) => file.size <= VIDEO_MAX_SIZE, 'Filmik musi być mniejszy niż 20MB.')
  .refine(
    (file) => VIDEO_MIME_TYPES.includes(file.type),
    'Filmik musi być w formacie MP4 lub WEBM.',
  );

const exerciseStepOneSchema = z.object({
  name: z
    .string()
    .min(1, 'Nazwa ćwiczenia jest wymagana.')
    .max(150, 'Nazwa ćwiczenia nie może być dłuższa niż 150 znaków.'),

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
        return normalizedLength <= MAX_DESCRIPTION_LENGTH;
      },
      {
        message: 'Opis ćwiczenia nie może być dłuższy niż 850 znaków.',
      },
    ),
  muscleGroup: z
    .array(z.string())
    .min(1, 'Wybierz przynajmniej jedną grupę mięśniową.')
    .max(8, 'Możesz wybrać maksymalnie 8 grup mięśniowych.'),
  equipment: z
    .array(z.string())
    .min(1, 'Wybierz przynajmniej jeden sprzęt.')
    .max(8, 'Możesz wybrać maksymalnie 8 sprzętów.'),
  hashtags: z.array(z.string()).max(50, 'Możesz dodać maksymalnie 50 hashtagów.'),
});

export const exerciseSchema = z.object({
  stepOne: exerciseStepOneSchema,
  stepTwo: exerciseStepTwoSchema,
});

export type ExerciseSchema = z.infer<typeof exerciseSchema>;
