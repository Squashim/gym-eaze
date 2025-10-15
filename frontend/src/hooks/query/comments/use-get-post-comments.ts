import { getComments } from '@/api/post';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { GetCommentsApiResponse } from '@/types/comment';
import type { CursorPaginatedResponse } from '@/types/filters';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetPostsComments(postId: string) {
  return useInfiniteQuery<CursorPaginatedResponse<GetCommentsApiResponse>, Error>({
    queryKey: QUERY_KEYS.POST_COMMENTS(postId),
    queryFn: ({ pageParam = null }) => getComments(postId, pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    refetchInterval: 1000 * 60 * 3,
    retry: false,
  });
}

export default useGetPostsComments;
