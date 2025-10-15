import { z } from 'zod';
import emailValidator from './validators/email-validator';
import { passwordValidator } from './validators/password-validator';

export const linkGoogleAccountSchema = z.object({
  email: emailValidator,
  password: passwordValidator,
  token: z.string(),
});

export type LinkGoogleAccountSchema = z.infer<typeof linkGoogleAccountSchema>;
