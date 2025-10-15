import { z } from 'zod';
import { passwordValidator } from './validators/password-validator';

const loginValidator = z
  .string()
  .min(1, 'Login jest wymagany.')
  .superRefine((val, ctx) => {
    const usernameRegex = /^(?=.*[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ])[0-9a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]{6,16}$/;
    const emailRegex =
      /^(?!\.)(?!.*\.\.)([A-Z0-9_'+-.]*)[A-Z0-9_'+-]@([A-Z0-9][A-Z0-9-]*\.)+[A-Z]{2,}$/i;

    const isEmail = emailRegex.test(val);
    const isUsername = usernameRegex.test(val);

    if (!isUsername && !isEmail) {
      ctx.addIssue({
        code: 'custom',
        message: 'Nieprawidłowy login.',
      });
    }
  });

export const loginSchema = z.object({
  login: loginValidator,
  password: passwordValidator,
  rememberMe: z.boolean().optional(),
});

export type LoginSchema = z.infer<typeof loginSchema>;
