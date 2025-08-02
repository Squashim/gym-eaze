import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { ThemeProvider } from './components/theme-provider';
import { BrowserRouter as Router } from 'react-router-dom';
import App from './app';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <Router>
        <App />
      </Router>
    </ThemeProvider>
  </StrictMode>,
);
