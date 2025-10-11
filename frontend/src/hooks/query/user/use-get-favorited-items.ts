import { getFavoritedItems } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useUser } from '@/context/user';
import type { UserFavoritesApiResponse } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

type FavoriteItemType = keyof UserFavoritesApiResponse;

function useGetFavoritedItems(type: FavoriteItemType, options?: { enabled?: boolean }) {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_FAVORITED_ITEMS(user?._id ?? ''),
    queryFn: getFavoritedItems,
    retry: false,
    select: (data) => new Set(data[type]),
    enabled: !!user && (options?.enabled ?? true),
  });
}

export default useGetFavoritedItems;
