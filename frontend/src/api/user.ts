import API from './api-client';
import { type UserFavoritesApiResponse, type UserLikesApiResponse } from '@/types/user';
import { USER as endpoints } from '@/constants/endpoints';

const getLikedItems = async (): Promise<UserLikesApiResponse> => {
  const { data } = await API.get<UserLikesApiResponse>(endpoints.likedItems);
  return data;
};

const getFavoritedItems = async (): Promise<UserFavoritesApiResponse> => {
  const { data } = await API.get<UserFavoritesApiResponse>(endpoints.favoritedItems);
  return data;
};

export { getLikedItems, getFavoritedItems };
