import type { ExerciseQueryParams, PaginatedQueryParams } from '@/types/filters';

export const QUERY_KEYS = {
  AUTH_USER: ['user', 'authenticated'],
  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],
  EXERCISES: (params: ExerciseQueryParams) => ['exercises', params],
  EXERCISE: (id: string) => ['exercises', id],
  FAVORITE_EXERCISES: (params: PaginatedQueryParams) => ['exercises', 'favorite', params],
  USER_EXERCISES: (userId: string, params: PaginatedQueryParams) => [
    'user',
    userId,
    'exercises',
    params,
  ],
  USER_LIKED_ITEMS: (userId: string) => ['user', userId, 'liked-items'],
  USER_FAVORITED_ITEMS: (userId: string) => ['user', userId, 'favorited-items'],
} as const;
