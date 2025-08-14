import { logout } from '@/api/auth';
import { QUERY_KEYS } from '@/constants/query-keys';
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

  return {
    logout: logoutMutation,
  };
}
