import { getAll } from '@/api/post';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { CursorPaginatedResponse, PostQueryParams } from '@/types/filters';
import type { GetPostsApiResponse } from '@/types/post';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetPosts(params: PostQueryParams) {
  return useInfiniteQuery<CursorPaginatedResponse<GetPostsApiResponse>, Error>({
    queryKey: QUERY_KEYS.POSTS(params),
    queryFn: ({ pageParam = null }) =>
      getAll({ ...params, cursor: pageParam as string | null | undefined }),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
  });
}

export default useGetPosts;
