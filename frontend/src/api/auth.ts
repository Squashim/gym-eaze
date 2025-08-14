import type { LinkGoogleAccountPayload, LoginPayload, SignupPayload } from '@/types/auth';
import API from './api-client';
import { type AuthenticatedUser } from '@/types/user';
import type { ApiResponse, LoginApiResponse } from '@/types/api';

const endpoints = {
  login: 'auth/login',
  googleLogin: 'auth/google',
  linkGoogleAccount: 'auth/link/google',
  register: 'auth/register',
  logout: 'auth/logout',
  refresh: 'auth/refresh',
  me: 'auth/me',
} as const;

const signup = async (payload: SignupPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.register, payload);
  return data;
};

const login = async (payload: LoginPayload): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.login, payload);
  return data;
};

export const googleLogin = async (token: string): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.googleLogin, { token });
  return data;
};

export const linkGoogleAccount = async (
  payload: LinkGoogleAccountPayload,
): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.linkGoogleAccount, payload);
  return data;
};

const logout = async (): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.logout);
  return data;
};
const refreshToken = () => API.post(endpoints.refresh);

const getAuthenticatedUser = async (): Promise<AuthenticatedUser> => {
  const { data } = await API.get<AuthenticatedUser>(endpoints.me);
  return data;
};

export { signup, login, logout, refreshToken, getAuthenticatedUser };
