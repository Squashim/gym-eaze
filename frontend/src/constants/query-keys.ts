import type { ExerciseQueryParams } from '@/types/filters';

export const QUERY_KEYS = {
  AUTH_USER: ['authenticated-user'],
  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],
  EXERCISES: (params: ExerciseQueryParams) => ['exercises', params],
  EXERCISE: (id: string) => ['exercises', id],
} as const;
