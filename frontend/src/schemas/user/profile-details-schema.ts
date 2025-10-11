import { Goal } from '@/constants/enums/goals';
import { MEDIA } from '@/constants/media';
import { USER_PROFILE } from '@/constants/user';
import { z } from 'zod/v3';

const profilePictureValidator = z
  .instanceof(File, { message: 'Zdjęcie profilowe jest wymagane.' })
  .refine(
    (file) => file.size <= MEDIA.IMAGE.MAX_SIZE,
    'Zdjęcie profilowe musi być mniejsze niż 5MB.',
  )
  .refine(
    (file) => MEDIA.IMAGE.MIME_TYPES.includes(file.type),
    'Zdjęcie profilowe musi być w formacie JPG, PNG lub WEBP.',
  )
  .nullable()
  .optional();

export const profileDetailsSchema = z.object({
  profilePicture: profilePictureValidator,
  description: z
    .string()
    .max(
      USER_PROFILE.BIO_MAX_LENGTH,
      `Biogram nie może być dłuższy niż ${USER_PROFILE.BIO_MAX_LENGTH} znaków`,
    )
    .optional()
    .or(z.literal('')),
  goal: z.nativeEnum(Goal).optional(),
  pinnedBadges: z
    .array(z.string())
    .max(
      USER_PROFILE.MAX_PINNED_BADGES,
      `Możesz przypiąć maksymalnie ${USER_PROFILE.MAX_PINNED_BADGES} odznak`,
    )
    .optional(),
});

export type ProfileDetailsSchema = z.infer<typeof profileDetailsSchema>;
