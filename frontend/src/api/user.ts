import API from './api-client';
import {
  type UserFavoritesApiResponse,
  type UserFitnessDetailsApiResponse,
  type UserFitnessDetailsPayload,
  type UserLikesApiResponse,
  type UserPersonalDetailsApiResponse,
  type UserProfileDetailsPayload,
} from '@/types/user';
import { USER as endpoints } from '@/constants/endpoints';
import type { ApiResponse } from '@/types/api';

const getLikedItems = async (): Promise<UserLikesApiResponse> => {
  const { data } = await API.get<UserLikesApiResponse>(endpoints.likedItems);
  return data;
};

const getFavoritedItems = async (): Promise<UserFavoritesApiResponse> => {
  const { data } = await API.get<UserFavoritesApiResponse>(endpoints.favoritedItems);
  return data;
};

const getPersonalDetails = async (): Promise<UserPersonalDetailsApiResponse> => {
  const { data } = await API.get<UserPersonalDetailsApiResponse>(endpoints.personalDetails);
  return data;
};

const updatePersonalDetails = async (payload: UserProfileDetailsPayload): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.personalDetails, payload);
  return data;
};

const getFitnessDetails = async (): Promise<UserFitnessDetailsApiResponse> => {
  const { data } = await API.get<UserFitnessDetailsApiResponse>(endpoints.fitnessDetails);
  return data;
};

const updateFitnessDetails = async (payload: UserFitnessDetailsPayload): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.fitnessDetails, payload);
  return data;
};

export {
  getLikedItems,
  getFavoritedItems,
  getPersonalDetails,
  updatePersonalDetails,
  getFitnessDetails,
  updateFitnessDetails,
};
