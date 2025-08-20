import { type LoginSchema } from '@/schemas/login-schema';
import { type SignupSchema } from '@/schemas/signup-schema';
import type { UserRole } from './user';

export type AuthAccess = 'logged-in' | 'not-logged-in' | 'all';
export type LoginPayload = LoginSchema;
export type SignupPayload = Omit<SignupSchema, 'confirmPassword'>;
export type LinkGoogleAccountPayload = Omit<SignupPayload, 'username'> & {
  token: string;
};
export type AuthAccessControlProps = {
  allow: AuthAccess;
  role?: UserRole;
};
