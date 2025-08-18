import { getAuthenticatedUser } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
import { type AuthenticatedUser } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

function useGetLoggedUser() {
  return useQuery<AuthenticatedUser>({
    queryKey: QUERY_KEYS.AUTH_USER,
    queryFn: getAuthenticatedUser,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
  });
}

export default useGetLoggedUser;
