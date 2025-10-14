import { REPORT as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type { CreateReportPayload, GetReportsApiResponse } from '@/types/report';
import type { ApiResponse } from '@/types/api';
import type { PaginatedResponse, ReportQueryParams } from '@/types/filters';

const create = async (payload: CreateReportPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.base, payload);
  return data;
};

const getAll = async (
  params: ReportQueryParams,
): Promise<PaginatedResponse<GetReportsApiResponse>> => {
  const { data } = await API.post<PaginatedResponse<GetReportsApiResponse>>(endpoints.search, {
    filters: params.filters,
    page: params.page,
    pageSize: params.pageSize,
  });

  return data;
};

export { create, getAll };
