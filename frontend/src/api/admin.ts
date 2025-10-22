import { ADMIN as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type { ApiResponse } from '@/types/api';
import type { ItemType } from '@/constants/enums/item-type';

const deleteContent = async (itemId: string, itemType: ItemType): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.deleteContent(itemId, itemType));
  return data;
};

const blockUser = async (userId: string): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.blockUser(userId));
  return data;
};

export { deleteContent, blockUser };
