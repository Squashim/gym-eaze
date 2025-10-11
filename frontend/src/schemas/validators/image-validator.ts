import { MEDIA } from '@/constants/media';
import z from 'zod/v3';

const imageValidator = z
  .instanceof(File, { message: 'Obraz jest wymagany.' })
  .refine((file) => file.size <= MEDIA.IMAGE.MAX_SIZE, 'Obraz musi być mniejszy niż 5MB.')
  .refine(
    (file) => MEDIA.IMAGE.MIME_TYPES.includes(file.type),
    'Obraz musi być w formacie JPG, PNG lub WEBP.',
  );

export default imageValidator;
