import { describe, it, expect } from 'vitest';
import { screen, fireEvent, waitFor } from '@testing-library/react';
import Login from './login';
import { renderWithProviders } from 'test/utils';

describe('Login Page', () => {
  it('should render the login form', () => {
    renderWithProviders(<Login />);
    expect(screen.getByLabelText('Email lub nazwa użytkownika')).toBeInTheDocument();
    expect(screen.getByLabelText('Hasło')).toBeInTheDocument();
    expect(screen.getByLabelText('Zapamiętaj mnie')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Zaloguj się' })).toBeInTheDocument();
  });

  it('shows validation errors when submitting empty form', async () => {
    renderWithProviders(<Login />);
    fireEvent.click(screen.getByRole('button', { name: 'Zaloguj się' }));

    await waitFor(() => {
      expect(screen.getAllByText(/wymagan[ye]/i).length).toBe(2);
    });
  });

  it('should toggle remeber me checkbox', () => {
    renderWithProviders(<Login />);

    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).toHaveAttribute('aria-checked', 'false');

    fireEvent.click(checkbox);
    expect(checkbox).toHaveAttribute('aria-checked', 'true');
  });
});
