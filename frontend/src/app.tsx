import { Route, Routes } from 'react-router-dom';
import Signup from './pages/sign-up';
import Login from './pages/login/login';
import Home from './pages/home';
import Welcome from './pages/welcome';
import Error from './pages/error';
import { GoogleLinkProvider } from './context/google-link';
import AuthGuard from './guards/auth-guard';
import ExerciseCreator from './components/features/exercises/exercise-creator';

function App() {
  return (
    <Routes>
      <Route element={<AuthGuard allow="all" />}>
        <Route element={<Welcome />} index path="/" />
        <Route element={<div>Posty</div>} path="/social/browse" />
        <Route element={<div>Ćwiczenia</div>} path="/exercises/browse" />
        <Route element={<div>Pomoc</div>} path="/help" />
      </Route>

      <Route element={<AuthGuard allow="not-logged-in" />}>
        <Route
          element={
            <GoogleLinkProvider>
              <Login />
            </GoogleLinkProvider>
          }
          path="login"
        />
        <Route
          element={
            <GoogleLinkProvider>
              <Signup />
            </GoogleLinkProvider>
          }
          path="signup"
        />
      </Route>

      <Route element={<AuthGuard allow="logged-in" />}>
        <Route element={<Home />} index path="home" />

        <Route element={<ExerciseCreator />} path="exercises/create" />
      </Route>

      <Route element={<AuthGuard allow="logged-in" role="admin" />}>
        <Route element={<div>admin dashboard</div>} index path="dashboard" />
      </Route>

      <Route element={<Error />} path="*" />
    </Routes>
  );
}

export default App;
