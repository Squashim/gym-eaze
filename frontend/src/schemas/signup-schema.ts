import { z } from 'zod';
import emailValidator from './validators/email-validator';
import usernameValidator from './validators/username-validator';
import { confirmPasswordValidator, strictPasswordValidator } from './validators/password-validator';

export const signupSchema = z
  .object({
    username: usernameValidator,
    email: emailValidator,
    password: strictPasswordValidator,
    confirmPassword: confirmPasswordValidator.min(1, 'Potwierdzenie hasła jest wymagane.'),
  })
  .refine(({ password, confirmPassword }) => password === confirmPassword, {
    path: ['confirmPassword'],
    message: 'Hasła muszą być identyczne.',
  });

export type SignupSchema = z.infer<typeof signupSchema>;
