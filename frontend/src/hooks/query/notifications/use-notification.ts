import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';
import { clearAll, create } from '@/api/notification';

export function useNotification() {
  const createMutation = useMutation({
    mutationFn: create,
  });

  const clearMutation = useMutation({
    mutationFn: clearAll,

    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_NOTIFICATIONS });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania powiadomień. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createNotification: createMutation,
    clearNotifications: clearMutation,
  };
}
