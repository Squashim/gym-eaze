import { getMuscleGroups } from '@/api/utils';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { MuscleGroup } from '@/types/muscle-group';
import { useQuery } from '@tanstack/react-query';

function useGetMuscleGroups() {
  return useQuery<MuscleGroup[]>({
    queryKey: QUERY_KEYS.MUSCLE_GROUPS,
    queryFn: getMuscleGroups,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
  });
}

export default useGetMuscleGroups;
