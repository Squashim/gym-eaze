import { REPORT as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type {
  CreateReportPayload,
  GetReportApiResponse,
  GetReportsApiResponse,
  UpdateReportStatusPayload,
} from '@/types/report';
import type { ApiResponse } from '@/types/api';
import type { PaginatedResponse, ReportQueryParams } from '@/types/filters';
import type { ItemType } from '@/constants/enums/item-type';

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

const getReportItems = async (
  itemId: string,
  itemType: ItemType,
): Promise<GetReportApiResponse[]> => {
  const { data } = await API.get<GetReportApiResponse[]>(endpoints.byReport(itemId, itemType));
  return data;
};

const deleteReport = async (itemId: string, itemType: ItemType): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.byReport(itemId, itemType));
  return data;
};

const updateStatus = async (payload: UpdateReportStatusPayload): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(
    endpoints.updateStatus(payload.itemId, payload.itemType),
    {
      status: payload.status,
    },
  );
  return data;
};

export { create, getAll, getReportItems, updateStatus, deleteReport };
