import { EXERCISE as endpoints } from '@/constants/api/endpoints';
import type {
  BaseExerciseApiResponse,
  CreateExercisePayload,
  GetExerciseApiResponse,
  GetExercisesApiResponse,
  GetExercisesByIdsApiResponse,
  UpdateExercisePayload,
} from '@/types/exercise';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type {
  CursorPaginatedResponse,
  ExerciseQueryParams,
  PaginatedQueryParams,
  PaginatedResponse,
} from '@/types/filters';
import type { ApiResponse } from '@/types/api';
import type { GetCommentsApiResponse } from '@/types/comment';

const create = async (payload: CreateExercisePayload): Promise<BaseExerciseApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<BaseExerciseApiResponse>(endpoints.base, formData);
  return data;
};

const update = async (
  id: string,
  payload: UpdateExercisePayload,
): Promise<BaseExerciseApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.patch<BaseExerciseApiResponse>(endpoints.byId(id), formData);
  return data;
};

const remove = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.byId(id));
  return data;
};

const getAll = async (
  params: ExerciseQueryParams,
): Promise<PaginatedResponse<GetExercisesApiResponse>> => {
  const { data } = await API.post<PaginatedResponse<GetExercisesApiResponse>>(endpoints.search, {
    filters: params.filters,
    page: params.page,
    pageSize: params.pageSize,
    sortBy: params.sortBy,
  });

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

const getById = async (id: string): Promise<GetExerciseApiResponse> => {
  const { data } = await API.get<GetExerciseApiResponse>(endpoints.byId(id));
  return data;
};

const getByIds = async (ids: string[]): Promise<GetExercisesByIdsApiResponse[]> => {
  const { data } = await API.post<GetExercisesByIdsApiResponse[]>(endpoints.byIds, { ids });
  return data;
};

const getFavorite = async (
  params: PaginatedQueryParams,
): Promise<PaginatedResponse<GetExercisesApiResponse>> => {
  const { data } = await API.get<PaginatedResponse<GetExercisesApiResponse>>(endpoints.favorite, {
    params: { ...params },
  });

  return data;
};

const getByUser = async (
  userId: string,
  params: PaginatedQueryParams,
): Promise<PaginatedResponse<GetExercisesApiResponse>> => {
  const { data } = await API.get<PaginatedResponse<GetExercisesApiResponse>>(
    endpoints.byUser(userId),
    { params: { ...params } },
  );

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
  getById,
  getByIds,
  getAll,
  toggleLike,
  toggleFavorite,
  remove,
  getByUser,
  getFavorite,
  getComments,
};
