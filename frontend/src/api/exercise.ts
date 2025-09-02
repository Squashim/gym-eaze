import { EXERCISE as endpoints } from '@/constants/endpoints';
import type { CreateExercisePayload, Exercise as ExerciseApiResponse } from '@/types/exercise';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';

const create = async (payload: CreateExercisePayload): Promise<ExerciseApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<ExerciseApiResponse>(endpoints.base, formData);
  return data;
};

export { create };
