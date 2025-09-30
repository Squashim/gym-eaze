import { COMMENT as endpoints } from '@/constants/endpoints';
import type { ApiResponse } from '@/types/api';
import type {
  CreateCommentPayload,
  GetCommentsApiResponse,
  UpdateCommentPayload,
} from '@/types/comment';
import API from './api-client';
import type { CursorPaginatedResponse } from '@/types/filters';

const create = async (payload: CreateCommentPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.base, payload);
  return data;
};

const update = async (id: string, payload: UpdateCommentPayload): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.byId(id), payload);
  return data;
};

const remove = async (id: string): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.byId(id));
  return data;
};

const getReplies = async (
  id: string,
  cursor?: string | null,
): Promise<CursorPaginatedResponse<GetCommentsApiResponse>> => {
  const { data } = await API.get<CursorPaginatedResponse<GetCommentsApiResponse>>(
    endpoints.replies(id),
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

export { create, update, remove, toggleLike, getReplies };
