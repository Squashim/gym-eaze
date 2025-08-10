import { Navigate, Outlet } from 'react-router-dom';
import Navbar from '../navbar';
import { useUser } from '@/context/user';
import Loading from '@/pages/loading';

interface AuthLayoutProps {
  allow: 'logged-in' | 'not-logged-in' | 'all';
  role?: 'admin' | 'user';
}

const AuthLayout = ({ allow, role = 'user' }: AuthLayoutProps) => {
  const { user, isLoading } = useUser();
  const isLoggedIn = !!user;
  const isAdmin = user && user.role === 'admin';

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
      <Navbar />
      <main className="container flex min-dvh-with-navbar flex-col items-center justify-center">
        <Outlet />
      </main>
    </>
  );
};

export default AuthLayout;
