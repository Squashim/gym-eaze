import { getById } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { GetExerciseApiResponse } from '@/types/exercise';
import { useQuery } from '@tanstack/react-query';

function useGetExercise(id: string, options?: { enabled?: boolean }) {
  return useQuery<GetExerciseApiResponse>({
    queryKey: QUERY_KEYS.EXERCISE(id),
    queryFn: () => getById(id),
    retry: false,
    refetchOnWindowFocus: false,
    ...options,
  });
}

export default useGetExercise;
