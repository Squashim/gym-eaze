import { create } from '@/api/workout';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useWorkout() {
  const navigate = useNavigate();

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

  return {
    createWorkout: createMutation,
  };
}
