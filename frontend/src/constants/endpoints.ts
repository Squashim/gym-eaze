export const AUTH = {
  login: 'auth/login',
  googleLogin: 'auth/google',
  linkGoogleAccount: 'auth/link/google',
  register: 'auth/register',
  logout: 'auth/logout',
  refresh: 'auth/refresh',
  me: 'auth/me',
} as const;

export const EXERCISE = {
  base: 'exercises',
} as const;

export const UTILS = {
  equipment: 'equipment',
  muscleGroups: 'muscleGroup',
} as const;
