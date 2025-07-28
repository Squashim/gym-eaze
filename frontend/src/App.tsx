import type { PropsWithChildren } from 'react';
import './App.css';
import { ThemeProvider } from '@/components/theme-provider';

function App({ children }: PropsWithChildren) {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      {children}
    </ThemeProvider>
  );
}

export default App;
