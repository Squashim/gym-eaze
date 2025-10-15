import { z } from 'zod';
import usernameValidator from '../validators/username-validator';
import {
  confirmPasswordValidator,
  strictPasswordValidator,
} from '../validators/password-validator';

export const loginDetailsSchema = z
  .object({
    username: usernameValidator,
    password: strictPasswordValidator.optional().or(z.literal('')),
    confirmPassword: confirmPasswordValidator.optional().or(z.literal('')),
  })
  .refine(
    ({ password, confirmPassword }) => {
      if (!password || password === '') {
        return true;
      }

      return confirmPassword && confirmPassword !== '';
    },
    {
      path: ['confirmPassword'],
      message: 'Potwierdzenie hasła jest wymagane gdy podano nowe hasło.',
    },
  )
  .refine(
    ({ password, confirmPassword }) => {
      if (!password || password === '' || !confirmPassword || confirmPassword === '') {
        return true;
      }

      return password === confirmPassword;
    },
    {
      path: ['confirmPassword'],
      message: 'Hasła muszą być identyczne.',
    },
  );

export type LoginDetailsSchema = z.infer<typeof loginDetailsSchema>;
