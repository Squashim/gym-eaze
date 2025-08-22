import { signup } from '@/api/auth';
import { getApiErrorMsg, getSignupErrorInputField } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import type { FieldValues, Path, UseFormSetError } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useSignup<T extends FieldValues>(setError?: UseFormSetError<T>) {
  const navigate = useNavigate();

  const signupMutation = useMutation({
    mutationFn: signup,
    onSuccess: async (response) => {
      toast.success(response.message);
      await navigate('/login');
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        if (setError) {
          const inputField = getSignupErrorInputField(errorMsg);

          setError(inputField as Path<T>, { type: 'server', message: errorMsg });
        } else {
          toast.error(errorMsg);
        }
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas rejestracji. Spróbuj ponownie później.');
      }
    },
  });

  return {
    signup: signupMutation,
  };
}
