import { getLikedItems } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import type { UserLikesApiResponse } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

function useGetLikedItems() {
  const { user } = useUser();

  return useQuery<UserLikesApiResponse>({
    queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user?._id ?? ''),
    queryFn: getLikedItems,
    retry: false,
    refetchOnWindowFocus: false,
    enabled: !!user,
  });
}

export default useGetLikedItems;
