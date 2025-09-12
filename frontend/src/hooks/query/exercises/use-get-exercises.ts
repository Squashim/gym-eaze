import { getAll } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { ExerciseQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

function useGetExercises(params: ExerciseQueryParams) {
  return useQuery<PaginatedResponse<GetExercisesApiResponse>>({
    queryKey: QUERY_KEYS.EXERCISES(params),
    queryFn: () => getAll(params),
    retry: false,
  });
}

export default useGetExercises;
