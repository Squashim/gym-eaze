import { getById } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { WorkoutApiResponse } from '@/types/workout';
import { useQuery } from '@tanstack/react-query';

function useGetWorkout(id: string, options?: { enabled?: boolean }) {
  return useQuery<WorkoutApiResponse>({
    queryKey: QUERY_KEYS.WORKOUT(id),
    queryFn: () => getById(id),
    retry: false,
    refetchOnWindowFocus: false,
    ...options,
  });
}

export default useGetWorkout;
