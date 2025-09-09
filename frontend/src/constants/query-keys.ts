export const QUERY_KEYS = {
  AUTH_USER: ['authenticated-user'],
  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],
  EXERCISES: ['exercises'],
  EXERCISE: (id: string) => ['exercises', id],
} as const;
