import { Request } from 'express';
import { UserRole } from '../constants/roles';

export interface AuthRequest extends Request {
  userId?: string;
}

export interface ICurrentUser {
  _id: string;
  username: string;
  email: string;
  role: UserRole;
  profilePicture?: string;
  createdAt: Date;
  updatedAt: Date;
}
