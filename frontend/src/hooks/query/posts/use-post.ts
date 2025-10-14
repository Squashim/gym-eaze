import { create, remove, toggleLike, update } from '@/api/post';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useUser } from '@/context/user';
import usePostParamsQuery from '@/hooks/filters-pagination/use-post-params-query';
import { getApiErrorMsg, isProfilePostQuery } from '@/lib/utils';
import type { UpdatePostPayload } from '@/types/post';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function usePost() {
  const { user } = useUser();
  const { params } = usePostParamsQuery();

  const createMutation = useMutation({
    mutationFn: create,
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
      await queryClient.invalidateQueries({
        predicate: (query) => isProfilePostQuery(query.queryKey),
      });
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

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: UpdatePostPayload }) =>
      update(id, payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
      await queryClient.invalidateQueries({
        predicate: (query) => isProfilePostQuery(query.queryKey),
      });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas edycji posta. Spróbuj ponownie później.');
      }
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => remove(id),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      await queryClient.invalidateQueries({
        predicate: (query) => isProfilePostQuery(query.queryKey),
      });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania posta. Spróbuj ponownie później.',
        );
      }
    },
  });

  const toggleLikeMutation = useMutation({
    mutationFn: (id: string) => toggleLike(id),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      await queryClient.invalidateQueries({
        predicate: (query) => isProfilePostQuery(query.queryKey),
      });
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
    updatePost: updateMutation,
    deletePost: deleteMutation,
    toggleLikePost: toggleLikeMutation,
  };
}
