import useGetLoggedUser from '@/hooks/auth/use-get-logged-user';
import type { AuthenticatedUser } from '@/types/user';
import { createContext, useContext, type PropsWithChildren } from 'react';

type UserContextType = {
  user: AuthenticatedUser | null | undefined;
  isLoading: boolean;
};

const UserContext = createContext<UserContextType | undefined>(undefined);

function UserProvider({ children }: PropsWithChildren) {
  const { data: authenticatedUser, isLoading } = useGetLoggedUser();

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
