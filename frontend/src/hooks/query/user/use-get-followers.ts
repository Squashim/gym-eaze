import { getFollowers } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useQuery } from '@tanstack/react-query';

function useGetFollowers(options?: { enabled: boolean }) {
  return useQuery({
    queryKey: QUERY_KEYS.USER_FOLLOWERS,
    queryFn: getFollowers,
    retry: false,
    enabled: options?.enabled ?? true,
  });
}

export default useGetFollowers;
