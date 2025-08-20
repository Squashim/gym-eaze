import { logout } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
import { STORAGE_FLAGS } from '@/constants/storage-flags';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useLogout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const logoutMutation = useMutation({
    mutationFn: logout,
    onSuccess: (response) => {
      queryClient.setQueryData(QUERY_KEYS.AUTH_USER, null);
      localStorage.removeItem(STORAGE_FLAGS.wasLoggedIn);
      toast.success(response.message);
      void navigate('/login', { replace: true });
    },
    onError: (error) => {
      const errorMsg =
        getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd podczas wylogowywania.';

      const isTokenError =
        errorMsg === 'Brak tokenu odświeżającego' ||
        errorMsg === 'Brak tokenu autoryzacyjnego' ||
        errorMsg === 'Nieprawidłowy/wygasły token odświeżający';

      if (isTokenError) {
        queryClient.setQueryData(QUERY_KEYS.AUTH_USER, null);
        localStorage.removeItem(STORAGE_FLAGS.wasLoggedIn);
        void navigate('/login', { replace: true });
      } else {
        toast.error(errorMsg);
      }
    },
  });

  return {
    logout: logoutMutation,
  };
}
