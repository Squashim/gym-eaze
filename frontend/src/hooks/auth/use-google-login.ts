import { getAuthenticatedUser, googleLogin } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
import { STORAGE_FLAGS } from '@/constants/storage-flags';
import { useGoogleLink } from '@/context/google-link';
import { getApiErrorMsg, isGoogleLoginConflictError } from '@/lib/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useGoogleLogin() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { setGoogleLinkData } = useGoogleLink();

  const googleLoginMutation = useMutation({
    mutationFn: googleLogin,
    onSuccess: async (response) => {
      try {
        const user = await getAuthenticatedUser();
        const redirectPath = user.role === 'admin' ? '/dashboard' : '/home';
        const isFirstLogin = response.isFirstLogin ?? false;
        queryClient.setQueryData(QUERY_KEYS.AUTH_USER, user);
        toast.success(response.message);

        if (isFirstLogin) {
          sessionStorage.setItem(STORAGE_FLAGS.isFirstLogin, 'true');
        }

        localStorage.setItem(STORAGE_FLAGS.wasLoggedIn, 'true');

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
        toast.error(errorMsg);
        return;
      }

      if (isGoogleLoginConflictError(error)) {
        setGoogleLinkData(error.response!.data);
        return;
      }

      toast.error('Wystąpił nieoczekiwany błąd podczas logowania. Spróbuj ponownie później.');
    },
  });

  return {
    googleLogin: googleLoginMutation,
  };
}
