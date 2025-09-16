import { getLikedItems } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import type { UserLikesApiResponse } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

type LikedItemType = keyof UserLikesApiResponse;

function useGetLikedItems(type: LikedItemType) {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user?._id ?? ''),
    queryFn: getLikedItems,
    retry: false,
    enabled: !!user,
    select: (data) => new Set(data[type]),
  });
}

export default useGetLikedItems;
