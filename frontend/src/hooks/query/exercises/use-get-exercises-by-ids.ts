import { getByIds } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import { useQuery } from '@tanstack/react-query';
import { useCallback, useMemo } from 'react';

function useGetExercisesByIds(ids: string[]) {
  const stableKey = useMemo(() => QUERY_KEYS.EXERCISES_BY_IDS(ids.length), [ids.length]);
  const fetchExercises = useCallback(() => getByIds(ids), [ids]);

  return useQuery<GetExercisesByIdsApiResponse[]>({
    queryKey: stableKey,
    queryFn: fetchExercises,
    retry: false,
    enabled: ids.length > 0,
  });
}

export default useGetExercisesByIds;
