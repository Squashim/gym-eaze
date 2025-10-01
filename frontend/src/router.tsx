import { createBrowserRouter } from 'react-router-dom';
import AuthGuard from './guards/auth-guard';
import Welcome from './pages/welcome';
import ExerciseBrowser from './pages/exercises/browser';
import ExerciseDetails from './pages/exercises/details';
import Error from './pages/error';
import { GoogleLinkProvider } from './context/google-link';
import Login from './pages/login/login';
import Signup from './pages/sign-up';
import Home from './pages/home';
import ExerciseCreator from './pages/exercises/creator';
import UserExercises from './pages/exercises/user';
import FavoriteExercises from './pages/exercises/favorite';
import App from './app';
import Community from './pages/community';
import WorkoutCreator from './pages/workouts/creator';
import WorkoutBrowser from './pages/workouts/browser';
import WorkoutDetails from './pages/workouts/details';

const router = createBrowserRouter([
  {
    element: <App />,
    errorElement: <Error />,
    children: [
      {
        element: <AuthGuard allow="all" />,
        children: [
          { index: true, path: '/', element: <Welcome /> },
          { path: '/community', element: <Community /> },
          {
            path: '/exercises',
            children: [
              { path: 'browse', element: <ExerciseBrowser /> },
              {
                path: ':id',
                element: <ExerciseDetails />,
              },
            ],
          },
          {
            path: '/workouts',
            children: [
              { path: 'browse', element: <WorkoutBrowser /> },
              {
                path: ':id',
                element: <WorkoutDetails />,
              },
            ],
          },
          {
            path: '/help',
            element: <div>Pomoc</div>,
          },
        ],
      },
      {
        element: <AuthGuard allow="not-logged-in" />,
        children: [
          {
            path: 'login',
            element: (
              <GoogleLinkProvider>
                <Login />
              </GoogleLinkProvider>
            ),
          },
          {
            path: 'signup',
            element: (
              <GoogleLinkProvider>
                <Signup />
              </GoogleLinkProvider>
            ),
          },
        ],
      },
      {
        element: <AuthGuard allow="logged-in" />,
        children: [
          {
            index: true,
            path: 'home',
            element: <Home />,
          },
          {
            path: 'exercises',
            children: [
              { path: 'create', element: <ExerciseCreator mode="create" /> },
              { path: 'my', element: <UserExercises /> },
              {
                path: 'favorites',
                element: <FavoriteExercises />,
              },
              {
                path: ':id/edit',
                element: <ExerciseCreator mode="edit" />,
              },
            ],
          },
          {
            path: 'workouts',
            children: [{ path: 'create', element: <WorkoutCreator mode="create" /> }],
          },
        ],
      },
      {
        element: <AuthGuard allow="logged-in" role="admin" />,
        children: [
          {
            index: true,
            path: 'dashboard',
            element: <div>admin dashboard</div>,
          },
        ],
      },
    ],
  },
]);

export default router;
