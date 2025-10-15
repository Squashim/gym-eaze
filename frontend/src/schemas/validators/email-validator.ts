import { z } from 'zod';

const emailValidator = z
  .string()
  .min(1, 'Adres e-mail jest wymagany.')
  .regex(
    /^(?!\.)(?!.*\.\.)([A-Z0-9_'+-.]*)[A-Z0-9_'+-]@([A-Z0-9][A-Z0-9-]*\.)+[A-Z]{2,}$/i,
    'Nieprawidłowy adres e-mail.',
  );

export default emailValidator;
