import type { Notification } from './notification';

type UserRole = 'admin' | 'user';

export interface AuthenticatedUser {
  _id: string;
  username: string;
  email: string;
  role: UserRole;
  profilePicture?: string;
  notifications?: Notification[];
  createdAt: string;
  updatedAt: string;
}
