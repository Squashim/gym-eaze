import { WORKOUT as endpoints } from '@/constants/endpoints';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type { CreateWorkoutPayload, WorkoutApiResponse } from '@/types/workout';

const create = async (payload: CreateWorkoutPayload): Promise<WorkoutApiResponse> => {
  const parsedPayload = { ...payload, days: JSON.stringify(payload.days) };
  const formData = objectToFormData(parsedPayload);
  const { data } = await API.post<WorkoutApiResponse>(endpoints.base, formData);
  return data;
};

export { create };
