import { create, remove, toggleFavorite, toggleLike, update } from '@/api/workout';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import { getApiErrorMsg } from '@/lib/utils';
import type { UpdateWorkoutPayload } from '@/types/workout';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useWorkout() {
  const navigate = useNavigate();
  const { user } = useUser();

  const createMutation = useMutation({
    mutationFn: create,
    onSuccess: (response) => {
      toast.success('Plan został pomyślnie stworzony.');
      void navigate(`/workouts/${response._id}`, { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania planu. Spróbuj ponownie później.',
        );
      }
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: UpdateWorkoutPayload }) =>
      update(id, payload),
    onSuccess: (response) => {
      toast.success('Plan został pomyślnie zaktualizowany.');
      void navigate(`/workouts/${response._id}`, { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas edycji planu. Spróbuj ponownie później.');
      }
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => remove(id),
    onSuccess: async (response, id) => {
      queryClient.removeQueries({ queryKey: QUERY_KEYS.WORKOUT(id) });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      toast.success(response.message);
      await navigate('/workouts/browse', { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania planu. Spróbuj ponownie później.',
        );
      }
    },
  });

  const toggleLikeMutation = useMutation({
    mutationFn: (id: string) => toggleLike(id),
    onSuccess: async (response, id) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT(id) });
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

  const toggleFavoriteMutation = useMutation({
    mutationFn: (id: string) => toggleFavorite(id),
    onSuccess: async (response, id) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT(id) });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_FAVORITED_ITEMS(user!._id) });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania/usuwania do/z ulubionych. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createWorkout: createMutation,
    updateWorkout: updateMutation,
    deleteWorkout: deleteMutation,
    toggleLikeWorkout: toggleLikeMutation,
    toggleFavoriteWorkout: toggleFavoriteMutation,
  };
}
