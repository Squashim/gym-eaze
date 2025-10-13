import { getByUser } from '@/api/post';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { CursorPaginatedResponse } from '@/types/filters';
import type { GetPostsApiResponse } from '@/types/post';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetUserPosts(userId: string, options?: { enabled?: boolean }) {
  return useInfiniteQuery<CursorPaginatedResponse<GetPostsApiResponse>, Error>({
    queryKey: QUERY_KEYS.USER_POSTS(userId),
    queryFn: ({ pageParam = null }) => getByUser(userId, pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    retry: false,
    ...options,
  });
}

export default useGetUserPosts;
