import { type LoginSchema } from '@/schemas/login-schema';
import { type SignupSchema } from '@/schemas/signup-schema';

export type LoginPayload = LoginSchema;
export type SignupPayload = Omit<SignupSchema, 'confirmPassword'>;
export type LinkGoogleAccountPayload = Omit<SignupPayload, 'username'> & {
  token: string;
};
