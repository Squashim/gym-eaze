import { Country } from '@/constants/countries';
import { MEDIA } from '@/constants/forms';
import { Gender } from '@/constants/gender';
import { Goal } from '@/constants/goals';
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

const usernameValidator = z
  .string()
  .min(1, 'Nazwa użytkownika jest wymagana.')
  .min(6, 'Nazwa użytkownika musi mieć co najmniej 6 znaków.')
  .max(16, 'Nazwa użytkownika nie może być dłuższa niż 16 znaków.')
  .regex(/[0-9a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]/, 'Nazwa użytkownika może zawierać tylko litery i cyfry');

const passwordValidator = z
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
  })
  .optional()
  .or(z.literal(''));

const confirmPasswordValidator = z.string().optional().or(z.literal(''));
export const loginDetailsSchema = z
  .object({
    username: usernameValidator,
    password: passwordValidator,
    confirmPassword: confirmPasswordValidator,
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

const profilePictureValidator = z
  .instanceof(File, { message: 'Zdjęcie profilowe jest wymagane.' })
  .refine(
    (file) => file.size <= MEDIA.IMAGE_MAX_SIZE,
    'Zdjęcie profilowe musi być mniejsze niż 5MB.',
  )
  .refine(
    (file) => MEDIA.IMAGE_MIME_TYPES.includes(file.type),
    'Zdjęcie profilowe musi być w formacie JPG, PNG lub WEBP.',
  )
  .nullable()
  .optional();

export const profileDetailsSchema = z.object({
  profilePicture: profilePictureValidator,
  description: z
    .string()
    .max(150, 'Biogram nie może być dłuższy niż 150 znaków')
    .optional()
    .or(z.literal('')),
  goal: z.nativeEnum(Goal).optional(),
  pinnedBadges: z.array(z.string()).max(5, 'Możesz przypiąć maksymalnie 5 odznak').optional(),
});

export type ProfileDetailsSchema = z.infer<typeof profileDetailsSchema>;
