import { updateFitnessDetails, updateLoginDetails, updatePersonalDetails } from '@/api/user';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/query-keys';
import { getApiErrorMsg } from '@/lib/utils';
import type {
  UserFitnessDetailsPayload,
  UserLoginDetailsPayload,
  UserProfileDetailsPayload,
} from '@/types/user';
import { useMutation } from '@tanstack/react-query';
import type { FieldValues, UseFormSetError } from 'react-hook-form';
import { toast } from 'sonner';

export function useUserProfile<T extends FieldValues>(setError?: UseFormSetError<T>) {
  const updatePersonalDetailsMutation = useMutation({
    mutationFn: (payload: UserProfileDetailsPayload) => updatePersonalDetails(payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_PERSONAL_DETAILS });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas edycji. Spróbuj ponownie później.');
      }
    },
  });

  const updateFitnessDetailsMutation = useMutation({
    mutationFn: (payload: UserFitnessDetailsPayload) => updateFitnessDetails(payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_FITNESS_DETAILS });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas edycji. Spróbuj ponownie później.');
      }
    },
  });

  const updateLoginDetailsMutation = useMutation({
    mutationFn: (payload: UserLoginDetailsPayload) => updateLoginDetails(payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.AUTH_USER });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        if (setError) {
          setError('root', { type: 'server', message: errorMsg });
        } else {
          toast.error(errorMsg);
        }
      } else {
        toast.error('Wystąpił nieoczekiwany błąd podczas edycji. Spróbuj ponownie później.');
      }
    },
  });

  return {
    updatePersonalDetails: updatePersonalDetailsMutation,
    updateFitnessDetails: updateFitnessDetailsMutation,
    updateLoginDetails: updateLoginDetailsMutation,
  };
}
