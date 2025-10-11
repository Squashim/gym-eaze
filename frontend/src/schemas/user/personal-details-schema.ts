import { Country } from '@/constants/enums/countries';
import { USER_PROFILE } from '@/constants/user';
import { z } from 'zod/v3';

const nameRegex = /^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$/;

export const personalDetailsSchema = z.object({
  firstName: z
    .string()
    .min(
      USER_PROFILE.FIRST_NAME.MIN,
      `Imię musi mieć co najmniej ${USER_PROFILE.FIRST_NAME.MIN} znaki`,
    )
    .max(
      USER_PROFILE.FIRST_NAME.MAX,
      `Imię nie może być dłuższe niż ${USER_PROFILE.FIRST_NAME.MAX} znaków`,
    )
    .regex(nameRegex, 'Imię może zawierać tylko litery')
    .optional()
    .or(z.literal('')),
  lastName: z
    .string()
    .min(
      USER_PROFILE.LAST_NAME.MIN,
      `Nazwisko musi mieć co najmniej ${USER_PROFILE.LAST_NAME.MIN} znaki`,
    )
    .max(
      USER_PROFILE.LAST_NAME.MAX,
      `Nazwisko nie może być dłuższe niż ${USER_PROFILE.LAST_NAME.MAX} znaków`,
    )
    .regex(nameRegex, 'Nazwisko może zawierać tylko litery')
    .optional()
    .or(z.literal('')),
  country: z.nativeEnum(Country).optional(),
});

export type PersonalDetailsSchema = z.infer<typeof personalDetailsSchema>;
