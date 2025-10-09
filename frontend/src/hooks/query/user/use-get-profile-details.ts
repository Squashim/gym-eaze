import { getProfileDetails } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetProfileDetails() {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_PROFILE_DETAILS,
    queryFn: getProfileDetails,
    retry: false,
    enabled: !!user,
  });
}

export default useGetProfileDetails;
