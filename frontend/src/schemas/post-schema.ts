import { z } from 'zod';
import { POST } from '@/constants/post';
import { MEDIA } from '@/constants/media';

const imageValidator = z
  .instanceof(File, { message: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= MEDIA.IMAGE.MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => MEDIA.IMAGE.MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  )
  .nullable()
  .optional();

const videoValidator = z
  .instanceof(File, { message: 'Filmik jest wymagany.' })
  .refine((file) => file.size <= MEDIA.VIDEO.MAX_SIZE, 'Filmik musi być mniejszy niż 20MB.')
  .refine(
    (file) => MEDIA.VIDEO.MIME_TYPES.includes(file.type),
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
        return normalizedLength <= POST.MAX_CONTENT_LENGTH;
      },
      {
        message: `Treść nie może być dłuższa niż ${POST.MAX_CONTENT_LENGTH} znaków.`,
      },
    ),
  image: imageValidator,
  video: videoValidator,
  hashtags: z
    .array(z.string())
    .max(POST.MAX_HASHTAGS, `Możesz dodać maksymalnie ${POST.MAX_HASHTAGS} hashtagów.`),
});

export type PostSchema = z.infer<typeof postSchema>;
