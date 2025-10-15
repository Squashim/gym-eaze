import { getByUser } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { useQuery } from '@tanstack/react-query';

function useGetUserWorkouts(
  userId: string,
  params: PaginatedQueryParams,
  includePrivate: boolean = true,
) {
  return useQuery<PaginatedResponse<GetWorkoutsApiResponse>>({
    queryKey: QUERY_KEYS.USER_WORKOUTS(userId, params, includePrivate),
    queryFn: () => getByUser(userId, params, includePrivate),
    retry: false,
    enabled: !!userId,
  });
}

export default useGetUserWorkouts;
