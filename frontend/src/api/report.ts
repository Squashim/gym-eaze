import { REPORT as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type { CreateReportPayload } from '@/types/report';
import type { ApiResponse } from '@/types/api';

const create = async (payload: CreateReportPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.base, payload);
  return data;
};

export { create };
