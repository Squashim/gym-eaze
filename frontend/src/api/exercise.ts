import { EXERCISE as endpoints } from '@/constants/endpoints';
import type {
  CreateExercisePayload,
  Exercise as ExerciseApiResponse,
  UpdateExercisePayload,
} from '@/types/exercise';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type { ExerciseQueryParams, PaginatedResponse } from '@/types/filters';

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

const getAll = async (
  params: ExerciseQueryParams,
): Promise<PaginatedResponse<ExerciseApiResponse>> => {
  const { data } = await API.post<PaginatedResponse<ExerciseApiResponse>>(endpoints.search, {
    filters: params.filters,
    page: params.page,
    pageSize: params.pageSize,
    sortBy: params.sortBy,
  });

  return data;
};

const getById = async (id: string): Promise<ExerciseApiResponse> => {
  const { data } = await API.get<ExerciseApiResponse>(endpoints.byId(id));
  return data;
};

export { create, update, getById, getAll };
