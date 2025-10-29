import { getEquipment } from '@/api/utils';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { Equipment } from '@/types/equipment';
import { useQuery } from '@tanstack/react-query';

function useGetEquipment(options?: { enabled: boolean }) {
  return useQuery<Equipment[]>({
    queryKey: QUERY_KEYS.EQUIPMENT,
    queryFn: getEquipment,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
    ...options,
  });
}

export default useGetEquipment;
