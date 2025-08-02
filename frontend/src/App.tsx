import { Route, Routes } from 'react-router-dom';
import Signup from './pages/sign-up';
import Login from './pages/login';
import AuthLayout from './components/layouts/auth-layout';
import Home from './pages/home';
import Welcome from './pages/welcome';
import Error from './pages/error';

function App() {
  return (
    <>
      <Routes>
        <Route element={<AuthLayout allow="all" />}>
          <Route element={<Welcome />} index path="/" />
        </Route>

        <Route element={<AuthLayout allow="not-logged-in" />}>
          <Route element={<Login />} path="login" />
          <Route element={<Signup />} path="signup" />
        </Route>

        <Route element={<AuthLayout allow="logged-in" />}>
          <Route element={<Home />} index path="home" />
        </Route>

        <Route element={<AuthLayout allow="logged-in" role="admin" />}>
          <Route element={<div>admin dashboard</div>} index path="dashboard" />
        </Route>

        <Route element={<Error />} path="*" />
      </Routes>
    </>
  );
}

export default App;
