import { getAll } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { PaginatedResponse, WorkoutQueryParams } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { useQuery } from '@tanstack/react-query';

function useGetWorkouts(params: WorkoutQueryParams) {
  return useQuery<PaginatedResponse<GetWorkoutsApiResponse>>({
    queryKey: QUERY_KEYS.WORKOUTS(params),
    queryFn: () => getAll(params),
    retry: false,
  });
}

export default useGetWorkouts;
