import { z } from 'zod';

const emailValidator = z
  .string()
  .min(1, 'Adres e-mail jest wymagany.')
  .regex(
    /^(?!\.)(?!.*\.\.)([A-Z0-9_'+-.]*)[A-Z0-9_'+-]@([A-Z0-9][A-Z0-9-]*\.)+[A-Z]{2,}$/i,
    'Nieprawidłowy adres e-mail.',
  );

const passwordValidator = z
  .string()
  .min(1, 'Hasło jest wymagane.')
  .regex(
    /^(?=.*?[0-9])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^0-9A-Za-z]).{14,32}$/,
    'Nieprawidłowe hasło.',
  );

export const linkGoogleAccountSchema = z.object({
  email: emailValidator,
  password: passwordValidator,
  token: z.string(),
});

export type LinkGoogleAccountSchema = z.infer<typeof linkGoogleAccountSchema>;
