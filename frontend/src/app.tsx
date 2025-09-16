import { Outlet } from 'react-router-dom';
import { UserProvider } from './context/user';

function App() {
  return (
    <UserProvider>
      <Outlet />
    </UserProvider>
  );
}

export default App;
