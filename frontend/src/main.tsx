import { createRoot } from 'react-dom/client';
import './index.css';
import { ThemeProvider } from '@/context/theme';
import { RouterProvider } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import queryClient from '@/config/query-client';
import { Toaster } from '@/components/ui/sonner';
import toasterOptions from './config/toaster';
import { GoogleOAuthProvider } from '@react-oauth/google';
import router from './router';
import { StrictMode } from 'react';

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
      <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
        <QueryClientProvider client={queryClient}>
          <RouterProvider router={router} />
          <Toaster {...toasterOptions} />
        </QueryClientProvider>
      </ThemeProvider>
    </GoogleOAuthProvider>
  </StrictMode>,
);
