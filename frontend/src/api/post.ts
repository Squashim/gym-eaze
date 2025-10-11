import { POST as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import { objectToFormData, serializeQuery } from '@/lib/utils';
import type { ApiResponse } from '@/types/api';
import type { CreatePostPayload, GetPostsApiResponse, UpdatePostPayload } from '@/types/post';
import type { CursorPaginatedResponse, PostQueryParams } from '@/types/filters';
import type { GetCommentsApiResponse } from '@/types/comment';

const create = async (payload: CreatePostPayload): Promise<ApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<ApiResponse>(endpoints.base, formData);
  return data;
};

const update = async (id: string, payload: UpdatePostPayload): Promise<ApiResponse> => {
  const formData = objectToFormData(payload);
  if (payload.image === null) {
    formData.append('image', 'null');
  }
  if (payload.video === null) {
    formData.append('video', 'null');
  }
  const { data } = await API.patch<ApiResponse>(endpoints.byId(id), formData);
  return data;
};

const remove = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.byId(id));
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

const toggleLike = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.toggleLike(id));
  return data;
};

export { create, update, remove, getAll, toggleLike, getComments };
