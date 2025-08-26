import z from 'zod';

const IMAGE_MIME_TYPES = ['image/png', 'image/jpg', 'image/webp'];
const IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB

const VIDEO_MIME_TYPES = ['video/mp4', 'video/webm'];
const VIDEO_MAX_SIZE = 20 * 1024 * 1024; // 20MB

const imageValidator = z
  .instanceof(File, { error: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= IMAGE_MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => IMAGE_MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  );

const videoValidator = z
  .instanceof(File, { error: 'Filmik jest wymagany.' })
  .refine((file) => file.size <= VIDEO_MAX_SIZE, 'Filmik musi być mniejszy niż 20MB.')
  .refine(
    (file) => VIDEO_MIME_TYPES.includes(file.type),
    'Filmik musi być w formacie MP4 lub WEBM.',
  );

export const exerciseSchema = z.object({
  author: z.string(),
  name: z
    .string()
    .min(1, 'Nazwa ćwiczenia jest wymagana.')
    .max(150, 'Nazwa ćwiczenia nie może być dłuższa niż 150 znaków.'),
  description: z
    .string()
    .min(1, 'Opis ćwiczenia jest wymagany.')
    .max(850, 'Opis ćwiczenia nie może być dłuższy niż 850 znaków.'),
  image: imageValidator,
  video: videoValidator,
  visibility: z.enum(['public', 'private']),
  advancedLevel: z.enum(['easy', 'medium', 'hard']),
  muscleGroup: z.array(z.string()).min(1, 'Wybierz przynajmniej jedną grupę mięśniową.'),
  equipment: z
    .array(z.string())
    .min(1, 'Wybierz przynajmniej jeden sprzęt.')
    .max(8, 'Możesz wybrać maksymalnie 8 sprzętów.'),
  hashtags: z.array(z.string()).max(50, 'Możesz dodać maksymalnie 50 hashtagów.').optional(),
});

export type ExerciseSchema = z.infer<typeof exerciseSchema>;
