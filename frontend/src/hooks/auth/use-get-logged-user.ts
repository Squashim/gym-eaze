import { getAuthenticatedUser } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { type AuthenticatedUser } from '@/types/user';
import { useQuery } from '@tanstack/react-query';
import { useLocation } from 'react-router-dom';

function useGetLoggedUser() {
  const location = useLocation();
  const pathname = location.pathname;

  const shouldFetchUser = !['/login', '/signup'].includes(pathname);

  const query = useQuery<AuthenticatedUser>({
    queryKey: QUERY_KEYS.AUTH_USER,
    queryFn: getAuthenticatedUser,
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    gcTime: Infinity,
    retry: false,
    enabled: shouldFetchUser,
  });

  return query;
}

export default useGetLoggedUser;
