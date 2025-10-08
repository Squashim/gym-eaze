import { Country } from '@/constants/countries';
import { Gender } from '@/constants/gender';
import { z } from 'zod/v3';

const nameRegex = /^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$/;

export const personalDetailsSchema = z.object({
  firstName: z
    .string()
    .min(3, 'Imię musi mieć co najmniej 3 znaki')
    .max(25, 'Imię nie może być dłuższe niż 25 znaków')
    .regex(nameRegex, 'Imię może zawierać tylko litery')
    .optional()
    .or(z.literal('')),
  lastName: z
    .string()
    .min(3, 'Nazwisko musi mieć co najmniej 3 znaki')
    .max(25, 'Nazwisko nie może być dłuższe niż 25 znaków')
    .regex(nameRegex, 'Nazwisko może zawierać tylko litery')
    .optional()
    .or(z.literal('')),
  country: z.nativeEnum(Country).optional(),
});

export type PersonalDetailsSchema = z.infer<typeof personalDetailsSchema>;

export const fitnessDetailsSchema = z.object({
  age: z
    .number()
    .min(18, 'Wiek musi być co najmniej 18 lat')
    .max(120, 'Wiek nie może przekraczać 120 lat')
    .optional()
    .nullable(),
  weight: z
    .number()
    .min(10, 'Waga musi być co najmniej 10 kg')
    .max(500, 'Waga nie może przekraczać 500 kg')
    .optional()
    .nullable(),
  height: z
    .number()
    .min(50, 'Wzrost musi być co najmniej 50 cm')
    .max(300, 'Wzrost nie może przekraczać 300 cm')
    .optional()
    .nullable(),
  gender: z.nativeEnum(Gender).optional(),
});

export type FitnessDetailsSchema = z.infer<typeof fitnessDetailsSchema>;
