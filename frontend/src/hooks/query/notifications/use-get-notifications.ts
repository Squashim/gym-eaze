import { getAll } from '@/api/notification';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { CursorPaginatedResponse } from '@/types/filters';
import type { GetNotificationsApiResponse } from '@/types/notification';
import { useInfiniteQuery } from '@tanstack/react-query';

function useGetNotifications() {
  return useInfiniteQuery<CursorPaginatedResponse<GetNotificationsApiResponse>, Error>({
    queryKey: QUERY_KEYS.USER_NOTIFICATIONS,
    queryFn: ({ pageParam = null }) => getAll(pageParam as string | null | undefined),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
    initialPageParam: null,
    refetchInterval: 1000 * 60 * 2,
    retry: false,
  });
}

export default useGetNotifications;
