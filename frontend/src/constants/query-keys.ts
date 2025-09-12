import type { ExerciseQueryParams } from '@/types/filters';

export const QUERY_KEYS = {
  AUTH_USER: ['authenticated-user'],
  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],
  EXERCISES: (params: ExerciseQueryParams) => ['exercises', params],
  EXERCISE: (id: string) => ['exercises', id],
  USER_LIKED_ITEMS: (userId: string) => ['user', userId, 'liked-items'],
  USER_FAVORITED_ITEMS: (userId: string) => ['user', userId, 'favorited-items'],
} as const;
