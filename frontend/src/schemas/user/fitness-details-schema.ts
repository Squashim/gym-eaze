import { Gender } from '@/constants/enums/gender';
import { USER_PROFILE } from '@/constants/user';
import { z } from 'zod/v3';

export const fitnessDetailsSchema = z.object({
  age: z
    .number()
    .min(USER_PROFILE.AGE.MIN, `Wiek musi być co najmniej ${USER_PROFILE.AGE.MIN} lat`)
    .max(USER_PROFILE.AGE.MAX, `Wiek nie może przekraczać ${USER_PROFILE.AGE.MAX} lat`)
    .optional()
    .nullable(),
  weight: z
    .number()
    .min(USER_PROFILE.WEIGHT.MIN, `Waga musi być co najmniej ${USER_PROFILE.WEIGHT.MIN} kg`)
    .max(USER_PROFILE.WEIGHT.MAX, `Waga nie może przekraczać ${USER_PROFILE.WEIGHT.MAX} kg`)
    .optional()
    .nullable(),
  height: z
    .number()
    .min(USER_PROFILE.HEIGHT.MIN, `Wzrost musi być co najmniej ${USER_PROFILE.HEIGHT.MIN} cm`)
    .max(USER_PROFILE.HEIGHT.MAX, `Wzrost nie może przekraczać ${USER_PROFILE.HEIGHT.MAX} cm`)
    .optional()
    .nullable(),
  gender: z.nativeEnum(Gender).optional(),
});

export type FitnessDetailsSchema = z.infer<typeof fitnessDetailsSchema>;
