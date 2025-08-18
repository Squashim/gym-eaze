import type { InternalAxiosRequestConfig } from 'axios';

export interface AuthAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

export type ApiResponse = {
  message: string;
};

export type LoginApiResponse = ApiResponse & {
  isFirstLogin?: boolean;
};

export type GoogleLoginConflictResponse = {
  email: string;
  linkRequired: boolean;
  token: string;
};
