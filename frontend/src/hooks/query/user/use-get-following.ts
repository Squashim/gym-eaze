import { getFollowing } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useQuery } from '@tanstack/react-query';

function useGetFollowing(options?: { enabled: boolean }) {
  return useQuery({
    queryKey: QUERY_KEYS.USER_FOLLOWING,
    queryFn: getFollowing,
    retry: false,
    enabled: options?.enabled ?? true,
  });
}

export default useGetFollowing;
