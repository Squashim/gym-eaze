import API from './api-client';
import {
  type UserFavoritesApiResponse,
  type UserFitnessDetailsApiResponse,
  type UserFitnessDetailsPayload,
  type UserLikesApiResponse,
  type UserLoginDetailsPayload,
  type UserPersonalDetailsApiResponse,
  type UserPersonalDetailsPayload,
  type UserProfileDetailsApiResponse,
  type UserProfileDetailsPayload,
} from '@/types/user';
import { USER as endpoints } from '@/constants/endpoints';
import type { ApiResponse } from '@/types/api';
import type { BadgeApiResponse } from '@/types/badge';
import { objectToFormData } from '@/lib/utils';

const getLikedItems = async (): Promise<UserLikesApiResponse> => {
  const { data } = await API.get<UserLikesApiResponse>(endpoints.likedItems);
  return data;
};

const getFavoritedItems = async (): Promise<UserFavoritesApiResponse> => {
  const { data } = await API.get<UserFavoritesApiResponse>(endpoints.favoritedItems);
  return data;
};

const getBadges = async (): Promise<BadgeApiResponse[]> => {
  const { data } = await API.get<BadgeApiResponse[]>(endpoints.badges);
  return data;
};

const getPersonalDetails = async (): Promise<UserPersonalDetailsApiResponse> => {
  const { data } = await API.get<UserPersonalDetailsApiResponse>(endpoints.personalDetails);
  return data;
};

const updatePersonalDetails = async (payload: UserPersonalDetailsPayload): Promise<ApiResponse> => {
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

const updateLoginDetails = async (payload: UserLoginDetailsPayload): Promise<ApiResponse> => {
  const { data } = await API.patch<ApiResponse>(endpoints.loginDetails, payload);
  return data;
};

const remove = async (): Promise<ApiResponse> => {
  const { data } = await API.delete<ApiResponse>(endpoints.base);
  return data;
};

const getProfileDetails = async (): Promise<UserProfileDetailsApiResponse> => {
  const { data } = await API.get<UserProfileDetailsApiResponse>(endpoints.profileDetails);
  return data;
};

const updateProfileDetails = async (payload: UserProfileDetailsPayload): Promise<ApiResponse> => {
  const formData = objectToFormData(payload);
  const { data } = await API.patch<ApiResponse>(endpoints.profileDetails, formData);
  return data;
};

export {
  getLikedItems,
  getFavoritedItems,
  getBadges,
  getPersonalDetails,
  updatePersonalDetails,
  getFitnessDetails,
  updateFitnessDetails,
  updateLoginDetails,
  getProfileDetails,
  updateProfileDetails,
  remove,
};
