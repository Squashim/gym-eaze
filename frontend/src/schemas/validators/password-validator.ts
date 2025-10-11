import { z } from 'zod';

const passwordValidator = z
  .string()
  .min(1, 'Hasło jest wymagane.')
  .regex(
    /^(?=.*?[0-9])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^0-9A-Za-z]).{14,32}$/,
    'Nieprawidłowe hasło.',
  );

const strictPasswordValidator = z
  .string()
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

const confirmPasswordValidator = z.string();

export { passwordValidator, strictPasswordValidator, confirmPasswordValidator };
