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
  comments: (id: string) => `exercises/${id}/comments`,
} as const;

export const WORKOUT = {
  base: 'training-plans',
  search: 'training-plans/search',
  favorite: 'training-plans/favorite',
  byId: (id: string) => `training-plans/${id}`,
  byUser: (userId: string) => `training-plans/user/${userId}`,
  toggleLike: (id: string) => `training-plans/${id}/like`,
  toggleFavorite: (id: string) => `training-plans/${id}/favorite`,
  download: (id: string) => `training-plans/${id}/pdf`,
  comments: (id: string) => `training-plans/${id}/comments`,
} as const;

export const POST = {
  base: 'posts',
  toggleLike: (id: string) => `posts/${id}/like`,
  byId: (id: string) => `posts/${id}`,
  comments: (id: string) => `posts/${id}/comments`,
} as const;

export const COMMENT = {
  base: 'comments',
  replies: (id: string) => `comments/${id}/replies`,
  byId: (id: string) => `comments/${id}`,
  toggleLike: (id: string) => `comments/${id}/like`,
} as const;

export const USER = {
  base: 'user',
  badges: 'user/badges',
  likedItems: 'user/likes',
  favoritedItems: 'user/favorites',

  privacySettings: 'user/privacy-settings',
  loginDetails: 'user/login-details',
  profileDetails: 'user/profile/profile-details',
  personalDetails: 'user/profile/personal-details',
  fitnessDetails: 'user/profile/fitness-details',
};

export const UTILS = {
  equipment: 'equipment',
  muscleGroups: 'muscleGroup',
} as const;
