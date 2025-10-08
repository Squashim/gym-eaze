import type { Country } from '@/constants/countries';
import type { Notification } from './notification';
import type { Gender } from '@/constants/gender';

export type UserRole = 'admin' | 'user';

export interface AuthenticatedUser {
  _id: string;
  username: string;
  email: string;
  role: UserRole;
  profileDetails: {
    profilePicture?: string;
  };
  notifications?: Notification[];
  createdAt: string;
  updatedAt: string;
}

export type Author = Pick<AuthenticatedUser, '_id' | 'username' | 'profileDetails'>;

export type UserLikesApiResponse = {
  trainingPlans: string[];
  exercises: string[];
  posts: string[];
  comments: string[];
};

export type UserFavoritesApiResponse = Omit<UserLikesApiResponse, 'posts' | 'comments'>;

export type UserProfileDetailsPayload = {
  firstName?: string;
  lastName?: string;
  country?: Country;
};

export type UserPersonalDetailsApiResponse = UserProfileDetailsPayload & {
  isPersonalDetailsPublic: boolean;
};

export type UserFitnessDetailsPayload = {
  age?: number;
  height?: number;
  weight?: number;
  gender?: Gender;
};

export type UserFitnessDetailsApiResponse = UserFitnessDetailsPayload & {
  isFitnessDetailsPublic: boolean;
};

export type UserLoginDetailsPayload = {
  username?: string;
  password?: string;
};
