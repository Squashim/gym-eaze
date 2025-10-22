import { NOTIFICATION as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type { CursorPaginatedResponse } from '@/types/filters';
import type { ApiResponse } from '@/types/api';
import type { CreateNotificationPayload, GetNotificationsApiResponse } from '@/types/notification';

const create = async (payload: CreateNotificationPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.base, payload);
  return data;
};

const clearAll = async (): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.base);
  return data;
};

const getAll = async (
  cursor?: string | null,
): Promise<CursorPaginatedResponse<GetNotificationsApiResponse>> => {
  const { data } = await API.get<CursorPaginatedResponse<GetNotificationsApiResponse>>(
    endpoints.base,
    {
      params: { cursor },
    },
  );

  return data;
};

export { create, getAll, clearAll };
