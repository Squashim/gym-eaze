import { MEDIA } from '@/constants/media';
import z from 'zod/v3';

const videoValidator = z
  .instanceof(File, { message: 'Filmik jest wymagany.' })
  .refine((file) => file.size <= MEDIA.VIDEO.MAX_SIZE, 'Filmik musi być mniejszy niż 20MB.')
  .refine(
    (file) => MEDIA.VIDEO.MIME_TYPES.includes(file.type),
    'Filmik musi być w formacie MP4 lub WEBM.',
  );

export default videoValidator;
