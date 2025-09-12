import { getFavoritedItems } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import type { UserFavoritesApiResponse } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

function useGetFavoritedItems(options?: { enabled?: boolean }) {
  const { user } = useUser();

  return useQuery<UserFavoritesApiResponse>({
    queryKey: QUERY_KEYS.USER_FAVORITED_ITEMS(user?._id ?? ''),
    queryFn: getFavoritedItems,
    retry: false,
    refetchOnWindowFocus: false,
    enabled: !!user && (options?.enabled ?? true),
  });
}

export default useGetFavoritedItems;
