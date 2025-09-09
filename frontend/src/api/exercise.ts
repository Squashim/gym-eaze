import { EXERCISE as endpoints } from '@/constants/endpoints';
import type {
  CreateExercisePayload,
  Exercise as ExerciseApiResponse,
  UpdateExercisePayload,
} from '@/types/exercise';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';

const create = async (payload: CreateExercisePayload): Promise<ExerciseApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<ExerciseApiResponse>(endpoints.base, formData);
  return data;
};

const update = async (id: string, payload: UpdateExercisePayload): Promise<ExerciseApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.patch<ExerciseApiResponse>(endpoints.byId(id), formData);
  return data;
};

const getById = async (id: string): Promise<ExerciseApiResponse> => {
  const { data } = await API.get<ExerciseApiResponse>(endpoints.byId(id));
  return data;
};

export { create, update, getById };
