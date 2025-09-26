import type { ExerciseQueryParams, PaginatedQueryParams, PostQueryParams } from '@/types/filters';

export const QUERY_KEYS = {
  AUTH_USER: ['user', 'authenticated'],
  USER_LIKED_ITEMS: (userId: string) => ['user', userId, 'liked-items'],
  USER_FAVORITED_ITEMS: (userId: string) => ['user', userId, 'favorited-items'],

  POST: (id: string) => ['posts', id],
  POSTS: (params: PostQueryParams) => ['posts', params],

  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],

  EXERCISES: (params: ExerciseQueryParams) => ['exercises', params],
  EXERCISES_BY_IDS: (length: number) => ['exercises', 'by-ids', length],
  EXERCISE: (id: string) => ['exercises', id],
  FAVORITE_EXERCISES: (params: PaginatedQueryParams) => ['exercises', 'favorite', params],
  USER_EXERCISES: (userId: string, params: PaginatedQueryParams) => [
    'user',
    userId,
    'exercises',
    params,
  ],
} as const;
