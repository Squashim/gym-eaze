import { getAuthenticatedUser, linkGoogleAccount } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
import { SESSION_STORAGE_FLAGS } from '@/constants/session-storage-flags';
import { useGoogleLink } from '@/context/google-link';
import { getApiErrorMsg } from '@/lib/utils';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useLinkGoogleAccount() {
  const navigate = useNavigate();
  const { setGoogleLinkData } = useGoogleLink();
  const queryClient = useQueryClient();

  const linkGoogleAccountMutation = useMutation({
    mutationFn: linkGoogleAccount,
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
        toast.error(errorMsg + '. Nie udało się połączyć konta Google.');
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas łączenia konta Google. Spróbuj ponownie później.',
        );
      }

      setGoogleLinkData(undefined);
    },
  });

  return {
    linkGoogleAccount: linkGoogleAccountMutation,
  };
}
