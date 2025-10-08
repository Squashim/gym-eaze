import { getComments } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { GetCommentsApiResponse } from '@/types/comment';
import type { CursorPaginatedResponse } from '@/types/filters';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetWorkoutComments(workoutId: string) {
  return useInfiniteQuery<CursorPaginatedResponse<GetCommentsApiResponse>, Error>({
    queryKey: QUERY_KEYS.WORKOUT_COMMENTS(workoutId),
    queryFn: ({ pageParam = null }) =>
      getComments(workoutId, pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    refetchInterval: 1000 * 60 * 3,
    retry: false,
  });
}

export default useGetWorkoutComments;
