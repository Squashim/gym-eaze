import type {
  ExerciseQueryParams,
  PaginatedQueryParams,
  PostQueryParams,
  WorkoutQueryParams,
} from '@/types/filters';

export const QUERY_KEYS = {
  AUTH_USER: ['user', 'authenticated'],
  USER_LIKED_ITEMS: (userId: string) => ['user', userId, 'liked-items'],
  USER_FAVORITED_ITEMS: (userId: string) => ['user', userId, 'favorited-items'],

  POST: (id: string) => ['posts', id],
  POSTS: (params: PostQueryParams) => ['posts', params],
  POST_COMMENTS: (postId: string) => ['posts', postId, 'comments'],

  COMMENT_REPLIES: (commentId: string) => ['comments', commentId, 'replies'],

  EQUIPMENT: ['equipment'],
  MUSCLE_GROUPS: ['muscle-groups'],

  EXERCISES: (params: ExerciseQueryParams) => ['exercises', params],
  EXERCISES_BY_IDS: (length: number) => ['exercises', 'by-ids', length],
  EXERCISE: (id: string) => ['exercises', id],
  EXERCISE_COMMENTS: (exerciseId: string) => ['exercises', exerciseId, 'comments'],
  FAVORITE_EXERCISES: (params: PaginatedQueryParams) => ['exercises', 'favorite', params],
  USER_EXERCISES: (userId: string, params: PaginatedQueryParams) => [
    'user',
    userId,
    'exercises',
    params,
  ],

  WORKOUT: (id: string) => ['workouts', id],
  WORKOUT_COMMENTS: (workoutId: string) => ['workouts', workoutId, 'comments'],
  WORKOUTS: (params: WorkoutQueryParams) => ['workouts', params],
  FAVORITE_WORKOUTS: (params: PaginatedQueryParams) => ['workouts', 'favorite', params],
  USER_WORKOUTS: (userId: string, params: PaginatedQueryParams) => [
    'user',
    userId,
    'workouts',
    params,
  ],
} as const;
