import { getAuthenticatedUser, googleLogin, login, logout, signup } from '@/api/auth';
import { getApiErrorMsg, getSignupErrorInputField } from '@/lib/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { FieldValues, Path, UseFormSetError } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

const AUTH_USER_QUERY_KEY = ['authenticated-user'];

export function useAuth<T extends FieldValues>(setError?: UseFormSetError<T>) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: async (response) => {
      try {
        const user = await getAuthenticatedUser();
        queryClient.setQueryData(AUTH_USER_QUERY_KEY, user);
        toast.success(response.message);
        await navigate('/dashboard', { replace: true });
      } catch {
        toast.error('Błąd podczas pobierania danych użytkownika.');
        await navigate('/', { replace: true });
      }
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        if (setError) {
          setError('root', { type: 'server', message: errorMsg });
        } else {
          toast.error(errorMsg);
        }
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas logowania. Spróbuj ponownie później.');
      }
    },
  });

  const signupMutation = useMutation({
    mutationFn: signup,
    onSuccess: async (response) => {
      toast.success(response.message);
      await navigate('/login');
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        if (setError) {
          const inputField = getSignupErrorInputField(errorMsg);

          setError(inputField as Path<T>, { type: 'server', message: errorMsg });
        } else {
          toast.error(errorMsg);
        }
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas rejestracji. Spróbuj ponownie później.');
      }
    },
  });

  const logoutMutation = useMutation({
    mutationFn: logout,
    onSuccess: (response) => {
      queryClient.setQueryData(AUTH_USER_QUERY_KEY, null);
      toast.success(response.message);
      void navigate('/', { replace: true });
    },
    onError: (error) => {
      const errorMsg =
        getApiErrorMsg(error) ||
        'Wystąpił nieoczekiwany błąd podczas wylogowywania. Spróbuj ponownie później.';

      toast.error(errorMsg);
    },
  });

  const googleLoginMutation = useMutation({
    mutationFn: googleLogin,
    onSuccess: async (response) => {
      try {
        const user = await getAuthenticatedUser();
        queryClient.setQueryData(AUTH_USER_QUERY_KEY, user);
        toast.success(response.message);
        await navigate('/dashboard', { replace: true });
      } catch {
        toast.error('Błąd podczas pobierania danych użytkownika.');
        await navigate('/', { replace: true });
      }
    },
    onError: (error) => {
      const errorMsg =
        getApiErrorMsg(error) ||
        'Wystąpił nieoczekiwany błąd podczas logowania. Spróbuj ponownie później.';

      toast.error(errorMsg);
    },
  });

  return {
    login: loginMutation,
    signup: signupMutation,
    logout: logoutMutation,
    googleLogin: googleLoginMutation,
  };
}
