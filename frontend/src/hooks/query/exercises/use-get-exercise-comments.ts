import { getComments } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { GetCommentsApiResponse } from '@/types/comment';
import type { CursorPaginatedResponse } from '@/types/filters';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetExerciseComments(exerciseId: string) {
  return useInfiniteQuery<CursorPaginatedResponse<GetCommentsApiResponse>, Error>({
    queryKey: QUERY_KEYS.EXERCISE_COMMENTS(exerciseId),
    queryFn: ({ pageParam = null }) =>
      getComments(exerciseId, pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    refetchInterval: 1000 * 60 * 3,
    retry: false,
  });
}

export default useGetExerciseComments;
