import { create, toggleLike } from '@/api/post';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import usePostParamsQuery from '@/hooks/use-post-params-query';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function usePost() {
  const { user } = useUser();
  const { params } = usePostParamsQuery();

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

  const toggleLikeMutation = useMutation({
    mutationFn: (id: string) => toggleLike(id),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania/usuwania polubienia. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createPost: createMutation,
    toggleLikePost: toggleLikeMutation,
  };
}
