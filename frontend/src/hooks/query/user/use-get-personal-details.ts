import { getPersonalDetails } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetPersonalDetails() {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_PERSONAL_DETAILS,
    queryFn: getPersonalDetails,
    retry: false,
    enabled: !!user,
  });
}

export default useGetPersonalDetails;
