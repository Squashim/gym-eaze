import { getAuthenticatedUser, login } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
import { SESSION_STORAGE_FLAGS } from '@/constants/session-storage-flags';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { FieldValues, UseFormSetError } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useLogin<T extends FieldValues>(setError?: UseFormSetError<T>) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: login,
    onSuccess: async (response) => {
      try {
        const user = await getAuthenticatedUser();
        const redirectPath = user.role === 'admin' ? '/dashboard' : '/home';
        const isFirstLogin = response.isFirstLogin ?? false;
        queryClient.setQueryData(QUERY_KEYS.AUTH_USER, user);
        toast.success(response.message);

        if (isFirstLogin) {
          sessionStorage.setItem(SESSION_STORAGE_FLAGS.isFirstLogin, 'true');
        }

        await navigate(redirectPath, {
          replace: true,
        });
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

  return {
    login: loginMutation,
  };
}
