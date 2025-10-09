import { getBadges } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetBadges() {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_BADGES,
    queryFn: getBadges,
    retry: false,
    enabled: !!user,
  });
}

export default useGetBadges;
