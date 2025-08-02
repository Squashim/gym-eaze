import { Navigate, Outlet } from 'react-router-dom';
import Navbar from '../navbar';

interface AuthLayoutProps {
  allow: 'logged-in' | 'not-logged-in' | 'all';
  role?: 'admin' | 'user';
}

const AuthLayout = ({ allow, role = 'user' }: AuthLayoutProps) => {
  const isLoggedIn = false; // TODO: Replace with actual authentication logic
  const isAdmin = false; // TODO: Replace with actual admin check logic

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
