import { render } from '@testing-library/react';
import { ThemeProvider } from '@/context/theme';
import { BrowserRouter } from 'react-router-dom';

export function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ThemeProvider>
      <BrowserRouter>{ui}</BrowserRouter>
    </ThemeProvider>,
  );
}
