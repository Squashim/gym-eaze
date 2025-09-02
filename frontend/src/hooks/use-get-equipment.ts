import { getEquipment } from '@/api/utils';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { Equipment } from '@/types/equipment';
import { useQuery } from '@tanstack/react-query';

function useGetEquipment() {
  return useQuery<Equipment[]>({
    queryKey: QUERY_KEYS.EQUIPMENT,
    queryFn: getEquipment,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
  });
}

export default useGetEquipment;
