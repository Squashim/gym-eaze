import { getAuthenticatedUser } from '@/api/auth';
import { type AuthenticatedUser } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

function useGetLoggedUser() {
  return useQuery<AuthenticatedUser>({
    queryKey: ['authenticated-user'],
    queryFn: () => getAuthenticatedUser(),
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
  });
}

export default useGetLoggedUser;
