import { getUserProfile } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetUserProfile(username: string) {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_PROFILE(username),
    queryFn: () => getUserProfile(username),
    retry: false,
    enabled: !!user,
  });
}

export default useGetUserProfile;
