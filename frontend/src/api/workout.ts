import { WORKOUT as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type {
  CreateWorkoutPayload,
  UpdateWorkoutPayload,
  GetWorkoutApiResponse,
  WorkoutApiResponse,
  GetWorkoutsApiResponse,
} from '@/types/workout';
import type { ApiResponse } from '@/types/api';
import type {
  CursorPaginatedResponse,
  PaginatedQueryParams,
  PaginatedResponse,
  WorkoutQueryParams,
} from '@/types/filters';
import type { GetCommentsApiResponse } from '@/types/comment';

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

const toggleLike = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.toggleLike(id));
  return data;
};

const toggleFavorite = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.toggleFavorite(id));
  return data;
};

const getById = async (id: string): Promise<GetWorkoutApiResponse> => {
  const { data } = await API.get<GetWorkoutApiResponse>(endpoints.byId(id));
  return data;
};

const getAll = async (
  params: WorkoutQueryParams,
): Promise<PaginatedResponse<GetWorkoutsApiResponse>> => {
  const { data } = await API.post<PaginatedResponse<GetWorkoutsApiResponse>>(endpoints.search, {
    filters: params.filters,
    page: params.page,
    pageSize: params.pageSize,
    sortBy: params.sortBy,
  });

  return data;
};

const getFavorite = async (
  params: PaginatedQueryParams,
): Promise<PaginatedResponse<GetWorkoutsApiResponse>> => {
  const { data } = await API.get<PaginatedResponse<GetWorkoutsApiResponse>>(endpoints.favorite, {
    params: { ...params },
  });

  return data;
};

const getByUser = async (
  userId: string,
  params: PaginatedQueryParams,
): Promise<PaginatedResponse<GetWorkoutsApiResponse>> => {
  const { data } = await API.get<PaginatedResponse<GetWorkoutsApiResponse>>(
    endpoints.byUser(userId),
    { params: { ...params } },
  );

  return data;
};

const download = async (id: string): Promise<Blob> => {
  const { data } = await API.get<Blob>(endpoints.download(id), { responseType: 'blob' });
  return data;
};

const getComments = async (
  id: string,
  cursor?: string | null,
): Promise<CursorPaginatedResponse<GetCommentsApiResponse>> => {
  const { data } = await API.get<CursorPaginatedResponse<GetCommentsApiResponse>>(
    endpoints.comments(id),
    {
      params: { cursor },
    },
  );
  return data;
};

export {
  create,
  update,
  remove,
  toggleLike,
  toggleFavorite,
  getById,
  download,
  getAll,
  getByUser,
  getFavorite,
  getComments,
};
