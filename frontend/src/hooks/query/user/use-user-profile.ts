import {
  remove,
  updateFitnessDetails,
  updateLoginDetails,
  updatePersonalDetails,
  updatePrivacySettings,
  updateProfileDetails,
} from '@/api/user';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/query-keys';
import { getApiErrorMsg } from '@/lib/utils';
import type {
  UserFitnessDetailsPayload,
  UserLoginDetailsPayload,
  UserPersonalDetailsPayload,
  UserPrivacySettings,
  UserProfileDetailsPayload,
} from '@/types/user';
import { useMutation } from '@tanstack/react-query';
import type { FieldValues, UseFormSetError } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export function useUserProfile<T extends FieldValues>(setError?: UseFormSetError<T>) {
  const navigate = useNavigate();

  const updatePersonalDetailsMutation = useMutation({
    mutationFn: (payload: UserPersonalDetailsPayload) => updatePersonalDetails(payload),
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

  const updateProfileDetailsMutation = useMutation({
    mutationFn: (payload: UserProfileDetailsPayload) => updateProfileDetails(payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.AUTH_USER });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_PROFILE_DETAILS });
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

  const updatePrivacySettingsMutation = useMutation({
    mutationFn: (payload: UserPrivacySettings) => updatePrivacySettings(payload),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_PRIVACY_SETTINGS });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas zmiany ustawień. Spróbuj ponownie później.',
        );
      }
    },
  });

  const removeUserMutation = useMutation({
    mutationFn: remove,
    onSuccess: (response) => {
      queryClient.clear();
      toast.success(response.message);
      void navigate('/login', { replace: true });
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania konta. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    updatePersonalDetails: updatePersonalDetailsMutation,
    updateFitnessDetails: updateFitnessDetailsMutation,
    updateLoginDetails: updateLoginDetailsMutation,
    updateProfileDetails: updateProfileDetailsMutation,
    updatePrivacySettings: updatePrivacySettingsMutation,
    removeAccount: removeUserMutation,
  };
}
