import { getFitnessDetails } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetFitnessDetails() {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_FITNESS_DETAILS,
    queryFn: getFitnessDetails,
    retry: false,
    enabled: !!user,
  });
}

export default useGetFitnessDetails;
