import { create } from '@/api/post';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function usePost() {
  const createMutation = useMutation({
    mutationFn: create,
    onSuccess: (response) => {
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania posta. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createPost: createMutation,
  };
}
