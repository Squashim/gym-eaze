import { POST as endpoints } from '@/constants/endpoints';
import API from './api-client';
import { objectToFormData, serializeQuery } from '@/lib/utils';
import type { ApiResponse } from '@/types/api';
import type { CreatePostPayload, GetPostsApiResponse } from '@/types/post';
import type { CursorPaginatedResponse, PostQueryParams } from '@/types/filters';

const create = async (payload: CreatePostPayload): Promise<ApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<ApiResponse>(endpoints.base, formData);
  return data;
};

const getAll = async (
  params?: PostQueryParams,
): Promise<CursorPaginatedResponse<GetPostsApiResponse>> => {
  const { data } = await API.get<CursorPaginatedResponse<GetPostsApiResponse>>(endpoints.base, {
    params,
    paramsSerializer: serializeQuery,
  });
  return data;
};

const toggleLike = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.toggleLike(id));
  return data;
};

export { create, getAll, toggleLike };
