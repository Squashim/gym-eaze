import { z } from 'zod';

const emailValidator = z
  .string()
  .min(1, 'Adres e-mail jest wymagany.')
  .regex(
    /^(?!\.)(?!.*\.\.)([A-Z0-9_'+-.]*)[A-Z0-9_'+-]@([A-Z0-9][A-Z0-9-]*\.)+[A-Z]{2,}$/i,
    'Nieprawidłowy adres e-mail.',
  );

const usernameValidator = z
  .string()
  .min(1, 'Nazwa użytkownika jest wymagana.')
  .min(6, 'Nazwa użytkownika musi mieć co najmniej 6 znaków.')
  .max(16, 'Nazwa użytkownika nie może być dłuższa niż 16 znaków.')
  .regex(/[0-9a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]/, 'Nazwa użytkownika może zawierać tylko litery i cyfry');

const passwordValidator = z
  .string()
  .min(1, 'Hasło jest wymagane.')
  .regex(/^.{14,20}$/, {
    message: 'Hasło musi mieć od 14 do 32 znaków.',
  })
  .regex(/(?=.*[A-Z])/, {
    message: 'Hasło musi zawierać co najmniej jedną wielką literę.',
  })
  .regex(/(?=.*[a-z])/, {
    message: 'Hasło musi zawierać co najmniej jedną małą literę.',
  })
  .regex(/(?=.*\d)/, {
    message: 'Hasło musi zawierać co najmniej jedną cyfrę.',
  })
  .regex(/[$&+,:;=?@#|'<>.^*()%!-]/, {
    message: 'Hasło musi zawierać co najmniej jeden znak specjalny.',
  });

const confirmPasswordValidator = z.string().min(1, 'Potwierdzenie hasła jest wymagane.');

export const signupSchema = z
  .object({
    username: usernameValidator,
    email: emailValidator,
    password: passwordValidator,
    confirmPassword: confirmPasswordValidator,
  })
  .refine(({ password, confirmPassword }) => password === confirmPassword, {
    path: ['confirmPassword'],
    message: 'Hasła muszą być identyczne.',
  });

export type SignupSchema = z.infer<typeof signupSchema>;
