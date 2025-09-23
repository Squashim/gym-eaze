import { z } from 'zod';

const IMAGE_MIME_TYPES = ['image/png', 'image/jpg', 'image/webp'];
const IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB

const VIDEO_MIME_TYPES = ['video/mp4', 'video/webm'];
const VIDEO_MAX_SIZE = 20 * 1024 * 1024; // 20MB

const MAX_CONTENT_LENGTH = 800;

const imageValidator = z
  .instanceof(File, { message: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= IMAGE_MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => IMAGE_MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  )
  .nullable()
  .optional();

const videoValidator = z
  .instanceof(File, { message: 'Filmik jest wymagany.' })
  .refine((file) => file.size <= VIDEO_MAX_SIZE, 'Filmik musi być mniejszy niż 20MB.')
  .refine(
    (file) => VIDEO_MIME_TYPES.includes(file.type),
    'Filmik musi być w formacie MP4 lub WEBM.',
  )
  .nullable()
  .optional();

export const postSchema = z.object({
  content: z
    .string()
    .min(1, 'Treść posta jest wymagana.')
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= MAX_CONTENT_LENGTH;
      },
      {
        message: 'Treść nie może być dłuższa niż 800 znaków.',
      },
    ),
  image: imageValidator,
  video: videoValidator,
  hashtags: z.array(z.string()).max(50, 'Możesz dodać maksymalnie 50 hashtagów.'),
});

export type PostSchema = z.infer<typeof postSchema>;
