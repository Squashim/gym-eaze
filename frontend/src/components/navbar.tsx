import { NavLink } from 'react-router-dom';
import Logo from './ui/logo';
import { useAuth } from '@/hooks/useAuth';
import { Button } from './ui/button';
import { useUser } from '@/context/user';

const Navbar = () => {
  const { logout } = useAuth();
  const { user } = useUser();

  const handleLogout = () => {
    logout.mutate();
  };
  return (
    <header className="sticky top-0 z-50 w-full bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 rounded-b-lg shadow-lg h-navbar">
      <nav className="container">
        <Logo redirect />
        <NavLink className={({ isActive }) => (isActive ? 'text-secondary ' : '')} to="/">
          Welcome (all)
        </NavLink>
        <NavLink className={({ isActive }) => (isActive ? 'text-secondary ' : '')} to="/home">
          Home (logged-in)
        </NavLink>
        <NavLink className={({ isActive }) => (isActive ? 'text-secondary ' : '')} to="/login">
          Login (not-logged-in)
        </NavLink>
        <NavLink className={({ isActive }) => (isActive ? 'text-secondary ' : '')} to="/signup">
          Signup (not-logged-in)
        </NavLink>
        <NavLink className={({ isActive }) => (isActive ? 'text-secondary ' : '')} to="/dashboard">
          Admin only
        </NavLink>
        {user && <Button onClick={handleLogout}>Wyloguj się</Button>}
      </nav>
    </header>
  );
};

export default Navbar;
