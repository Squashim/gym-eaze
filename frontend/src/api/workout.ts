import { WORKOUT as endpoints } from '@/constants/endpoints';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type {
  CreateWorkoutPayload,
  UpdateWorkoutPayload,
  WorkoutApiResponse,
} from '@/types/workout';
import type { ApiResponse } from '@/types/api';

const create = async (payload: CreateWorkoutPayload): Promise<WorkoutApiResponse> => {
  const parsedPayload = { ...payload, days: JSON.stringify(payload.days) };
  const formData = objectToFormData(parsedPayload);
  const { data } = await API.post<WorkoutApiResponse>(endpoints.base, formData);
  return data;
};

const update = async (id: string, payload: UpdateWorkoutPayload): Promise<WorkoutApiResponse> => {
  const parsedPayload = { ...payload, days: JSON.stringify(payload.days) };
  const formData = objectToFormData(parsedPayload);
  const { data } = await API.patch<WorkoutApiResponse>(endpoints.byId(id), formData);
  return data;
};

const remove = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.byId(id));
  return data;
};

const getById = async (id: string): Promise<WorkoutApiResponse> => {
  const { data } = await API.get<WorkoutApiResponse>(endpoints.byId(id));
  return data;
};

export { create, update, remove, getById };
