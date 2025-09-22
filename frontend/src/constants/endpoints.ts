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
  search: 'exercises/search',
  favorite: 'exercises/favorite',
  byId: (id: string) => `exercises/${id}`,
  byIds: 'exercises/batch',
  byUser: (userId: string) => `exercises/user/${userId}`,
  toggleLike: (id: string) => `exercises/${id}/like`,
  toggleFavorite: (id: string) => `exercises/${id}/favorite`,
} as const;

export const USER = {
  likedItems: 'user/likes',
  favoritedItems: 'user/favorites',
};

export const UTILS = {
  equipment: 'equipment',
  muscleGroups: 'muscleGroup',
} as const;
