import { Request } from 'express';
import { UserRole } from '../constants/roles';
import { INotificationSummary } from '../models/notificationModel';

export interface AuthRequest extends Request {
  userId?: string;
}

export interface ICurrentUser {
  _id: string;
  username: string;
  email: string;
  role: UserRole;
  profilePicture?: string;
  notifications?: INotificationSummary[];
  createdAt: Date;
  updatedAt: Date;
}
