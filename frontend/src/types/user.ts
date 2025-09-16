import type { Notification } from './notification';

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
};

export type UserFavoritesApiResponse = Omit<UserLikesApiResponse, 'posts'>;
