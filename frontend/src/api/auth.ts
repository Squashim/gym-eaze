import type { LoginPayload, SignupPayload } from '@/types/auth';
import API from './api-client';

const endpoints = {
  login: 'auth/login',
  register: 'auth/register',
  logout: 'auth/logout',
} as const;

export const signup = async (payload: SignupPayload) => API.post(endpoints.register, payload);
export const login = async (payload: LoginPayload) => API.post(endpoints.login, payload);
export const logout = async () => API.post(endpoints.logout);
