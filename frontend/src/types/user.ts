import type { Country } from '@/constants/enums/countries';
import type { Notification } from './notification';
import type { Gender } from '@/constants/enums/gender';
import type { BadgeApiResponse } from './badge';
import type { Goal } from '@/constants/enums/goals';

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

export type UserPersonalDetailsPayload = {
  firstName?: string;
  lastName?: string;
  country?: Country;
};

export type UserPersonalDetailsApiResponse = UserPersonalDetailsPayload & {
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

export type UserProfileDetailsPayload = {
  profilePicture?: File | null;
  description?: string;
  goal?: Goal;
  pinnedBadges?: string[];
};

export type UserProfileDetailsApiResponse = {
  profilePicture?: string;
  description?: string;
  goal?: Goal;
  pinnedBadges?: BadgeApiResponse[];
  isBadgesDetailsPublic: boolean;
  isGoalPublic: boolean;
};

export type UserLoginDetailsPayload = {
  username?: string;
  password?: string;
};

export type UserPrivacySettings = {
  data: {
    isPersonalDetailsPublic: boolean;
    isFitnessDetailsPublic: boolean;
    isBadgesDetailsPublic: boolean;
    isGoalPublic: boolean;
    isTrainingStatisticsPublic: boolean;
  };
};
