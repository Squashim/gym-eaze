import { z } from 'zod';

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

const passwordValidator = z
  .string()
  .min(1, 'Hasło jest wymagane.')
  .regex(
    /^(?=.*?[0-9])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^0-9A-Za-z]).{14,32}$/,
    'Nieprawidłowe hasło.',
  );

export const loginSchema = z.object({
  login: loginValidator,
  password: passwordValidator,
  rememberMe: z.boolean().optional(),
});

export type LoginSchema = z.infer<typeof loginSchema>;
