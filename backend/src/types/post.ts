import { Request } from 'express';
import { AuthRequest } from './auth';

export interface CreatePostData {
  author: string;
  content: string;
  hashtags?: string[];
}

export interface UpdatePostData {
  content?: string;
  hashtags?: string[];
  imageBuffer?: Buffer;
  videoBuffer?: Buffer;
}

export interface PostRequest extends Request {
  body: {
    content: string;
  };
  files?: {
    image?: Express.Multer.File[];
    video?: Express.Multer.File[];
  };
}

export type AuthPostRequest = AuthRequest & PostRequest;

export interface PostFilters {
  hashtags?: string[];
  followedOnly?: boolean;
  search?: string;
  excludeUserId?: string;
}
