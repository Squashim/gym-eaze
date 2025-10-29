import { getAll } from '@/api/active-workout';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { GetActiveWorkoutsApiResponse } from '@/types/active-workout';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

function useGetActiveWorkouts(
  userId: string,
  params: PaginatedQueryParams,
  includePrivate: boolean = true,
) {
  return useQuery<PaginatedResponse<GetActiveWorkoutsApiResponse>>({
    queryKey: QUERY_KEYS.USER_ACTIVE_WORKOUTS(userId, params, includePrivate),
    queryFn: () => getAll(userId, params, includePrivate),
    retry: false,
    enabled: !!userId,
  });
}

export default useGetActiveWorkouts;
