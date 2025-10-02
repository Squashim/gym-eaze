import { getReplies } from '@/api/comment';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { GetCommentsApiResponse } from '@/types/comment';
import type { CursorPaginatedResponse } from '@/types/filters';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetReplies(commentId: string, options?: { enabled: boolean }) {
  return useInfiniteQuery<CursorPaginatedResponse<GetCommentsApiResponse>, Error>({
    queryKey: QUERY_KEYS.COMMENT_REPLIES(commentId),
    queryFn: ({ pageParam = null }) =>
      getReplies(commentId, pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    enabled: options?.enabled ?? true,
    refetchInterval: 1000 * 60 * 2,
    retry: false,
  });
}

export default useGetReplies;
