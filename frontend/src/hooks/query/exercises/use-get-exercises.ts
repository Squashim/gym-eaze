import { getAll } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { Exercise } from '@/types/exercise';
import type { ExerciseQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

function useGetExercises(params: ExerciseQueryParams) {
  return useQuery<PaginatedResponse<Exercise>>({
    queryKey: QUERY_KEYS.EXERCISES(params),
    queryFn: () => getAll(params),
    retry: false,
    refetchOnWindowFocus: false,
  });
}

export default useGetExercises;
