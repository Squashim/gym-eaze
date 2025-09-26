import { POST as endpoints } from '@/constants/endpoints';
import API from './api-client';
import { objectToFormData } from '@/lib/utils';
import type { ApiResponse } from '@/types/api';
import type { CreatePostPayload } from '@/types/post';

const create = async (payload: CreatePostPayload): Promise<ApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.post<ApiResponse>(endpoints.base, formData);
  return data;
};

export { create };
