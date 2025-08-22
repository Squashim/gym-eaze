import type { LinkGoogleAccountPayload, LoginPayload, SignupPayload } from '@/types/auth';
import API, { REFRESH_API } from './api-client';
import { type AuthenticatedUser } from '@/types/user';
import type { ApiResponse, LoginApiResponse } from '@/types/api';
import { AUTH as endpoints } from '@/constants/endpoints';

const signup = async (payload: SignupPayload): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.register, payload);
  return data;
};

const login = async (payload: LoginPayload): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.login, payload);
  return data;
};

const googleLogin = async (token: string): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.googleLogin, { token });
  return data;
};

const linkGoogleAccount = async (payload: LinkGoogleAccountPayload): Promise<LoginApiResponse> => {
  const { data } = await API.post<LoginApiResponse>(endpoints.linkGoogleAccount, payload);
  return data;
};

const logout = async (): Promise<ApiResponse> => {
  const { data } = await API.post<ApiResponse>(endpoints.logout);
  return data;
};

const refreshToken = (): Promise<ApiResponse> => REFRESH_API.post(endpoints.refresh);

const getAuthenticatedUser = async (): Promise<AuthenticatedUser> => {
  const { data } = await API.get<AuthenticatedUser>(endpoints.me);
  return data;
};

export {
  signup,
  login,
  logout,
  refreshToken,
  getAuthenticatedUser,
  googleLogin,
  linkGoogleAccount,
};
