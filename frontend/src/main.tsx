import { createRoot } from 'react-dom/client';
import './index.css';
import { ThemeProvider } from '@/context/theme';
import { BrowserRouter as Router } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import App from './app';
import queryClient from '@/config/query-client';
import { Toaster } from '@/components/ui/sonner';
import toasterOptions from './config/toaster';
import { UserProvider } from './context/user';
import { GoogleOAuthProvider } from '@react-oauth/google';
import { StrictMode } from 'react';

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

createRoot(document.getElementById('root')!).render(
  <GoogleOAuthProvider clientId={clientId}>
    <StrictMode>
      <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
        <QueryClientProvider client={queryClient}>
          <Router>
            <UserProvider>
              <App />
            </UserProvider>
          </Router>
          <Toaster {...toasterOptions} />
        </QueryClientProvider>
      </ThemeProvider>
    </StrictMode>
  </GoogleOAuthProvider>,
);
