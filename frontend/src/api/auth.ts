import type { LoginPayload, SignupPayload } from '@/types/auth';
import API from './api-client';
import { type AuthenticatedUser } from '@/types/user';
import type { ApiResponse } from '@/types/api';

const endpoints = {
  login: 'auth/login',
  googleLogin: 'auth/google',
  register: 'auth/register',
  logout: 'auth/logout',
  refresh: 'auth/refresh',
  me: 'auth/me',
} as const;

const signup = async (payload: SignupPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.register, payload);
  return data;
};

const login = async (payload: LoginPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.login, payload);
  return data;
};

// export const googleLogin = async (payload: LoginPayload) => API.post(endpoints.login, payload);

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
