import { getReportItems } from '@/api/report';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { ItemType } from '@/constants/enums/item-type';
import { useQuery } from '@tanstack/react-query';

function useGetReportItems(itemId: string, itemType: ItemType, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: QUERY_KEYS.REPORT_ITEMS(itemId, itemType),
    queryFn: () => getReportItems(itemId, itemType),
    retry: false,
    ...options,
  });
}

export default useGetReportItems;
