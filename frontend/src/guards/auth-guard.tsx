import { Navigate, Outlet } from 'react-router-dom';
import { useUser } from '@/context/user';
import Loading from '@/pages/loading';
import Navbar from '@/components/layout/navbar';
import type { AuthAccessControlProps } from '@/types/auth';

const AuthGuard = ({ allow, role = 'user' }: AuthAccessControlProps) => {
  const { user, isLoading } = useUser();
  const isLoggedIn = !!user;
  const isAdmin = isLoggedIn && user.role === 'admin';

  if (isLoading) {
    return <Loading />;
  }

  if (allow === 'logged-in' && !isLoggedIn) {
    return <Navigate replace to="/login" />;
  }

  if (allow === 'not-logged-in' && isLoggedIn) {
    return <Navigate replace to="/home" />;
  }

  if (allow === 'logged-in' && role === 'admin' && !isAdmin) {
    return <Navigate replace to="/home" />;
  }

  return (
    <>
      <Navbar isAdmin={isAdmin} isLoggedIn={isLoggedIn} />
      <main className="container flex min-dvh-with-navbar flex-col items-center">
        <Outlet />
      </main>
    </>
  );
};

export default AuthGuard;
