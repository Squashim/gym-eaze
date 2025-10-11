import { getFavorite } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { useQuery } from '@tanstack/react-query';

function useGetFavoriteWorkouts(params: PaginatedQueryParams) {
  return useQuery<PaginatedResponse<GetWorkoutsApiResponse>>({
    queryKey: QUERY_KEYS.FAVORITE_WORKOUTS(params),
    queryFn: () => getFavorite(params),
    retry: false,
  });
}

export default useGetFavoriteWorkouts;
