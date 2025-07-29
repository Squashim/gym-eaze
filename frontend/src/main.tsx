import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.tsx';
import { H1 } from '@/components/ui/typography.tsx';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App>
      <H1>Hello World</H1>
    </App>
  </StrictMode>,
);
