import { updateFitnessDetails, updatePersonalDetails } from '@/api/user';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/query-keys';
import { getApiErrorMsg } from '@/lib/utils';
import type { UserFitnessDetailsPayload, UserProfileDetailsPayload } from '@/types/user';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function useUserProfile() {
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

  return {
    updatePersonalDetails: updatePersonalDetailsMutation,
    updateFitnessDetails: updateFitnessDetailsMutation,
  };
}
