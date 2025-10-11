import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import useGetLoggedUser from '@/hooks/auth/use-get-logged-user';
import type { AuthenticatedUser } from '@/types/user';
import { createContext, useContext, useEffect, type PropsWithChildren } from 'react';

type UserContextType = {
  user: AuthenticatedUser | null | undefined;
  isLoading: boolean;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

function UserProvider({ children }: PropsWithChildren) {
  const { data: authenticatedUser, isLoading } = useGetLoggedUser();

  useEffect(() => {
    const onStorage = (e: StorageEvent) => {
      if (e.key === 'wasLoggedIn') {
        if (e.newValue === 'true') {
          void queryClient.invalidateQueries({ queryKey: QUERY_KEYS.AUTH_USER });
        }
        if (e.newValue === null) {
          queryClient.setQueryData(QUERY_KEYS.AUTH_USER, null);
          queryClient.clear();
        }
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  return (
    <UserContext.Provider value={{ user: authenticatedUser, isLoading }}>
      {children}
    </UserContext.Provider>
  );
}

function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error('useUser() must be used within a UserProvider');
  return ctx;
}

// eslint-disable-next-line react-refresh/only-export-components
export { UserProvider, useUser };
