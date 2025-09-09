import { create, update } from '@/api/exercise';
import { getApiErrorMsg } from '@/lib/utils';
import type { UpdateExercisePayload } from '@/types/exercise';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useExercise() {
  const navigate = useNavigate();

  const createMutation = useMutation({
    mutationFn: create,
    onSuccess: (response) => {
      toast.success('Ćwiczenie zostało pomyślnie utworzone.');
      void navigate(`/exercises/${response._id}`, { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania ćwiczenia. Spróbuj ponownie później.',
        );
      }
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: UpdateExercisePayload }) =>
      update(id, payload),
    onSuccess: (response) => {
      toast.success('Ćwiczenie zostało pomyślnie zaktualizowane.');
      void navigate(`/exercises/${response._id}`, { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas edycji ćwiczenia. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createExercise: createMutation,
    updateExercise: updateMutation,
  };
}
