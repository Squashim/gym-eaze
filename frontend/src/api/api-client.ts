import axios, { AxiosError, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import type { AuthAxiosRequestConfig } from '@/types/api';
import { refreshToken } from './auth';
import { toast } from 'sonner';
import { STORAGE_FLAGS } from '@/constants/storage-flags';

if (!import.meta.env.VITE_API_URL) {
  throw new Error('VITE_API_URL is not defined');
}

const options: AxiosRequestConfig = {
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true,
};

const API = axios.create(options);
export const REFRESH_API = axios.create(options);

API.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AuthAxiosRequestConfig | undefined;

    if (
      error.response &&
      error.response.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest.url?.includes('/auth/logout')
    ) {
      originalRequest._retry = true;

      try {
        await refreshToken();
        return API(originalRequest);
      } catch (refreshError) {
        const wasLoggedIn = localStorage.getItem(STORAGE_FLAGS.wasLoggedIn) === 'true';

        if (wasLoggedIn) {
          toast.error('Token wygasł. Zaloguj się ponownie.');
          localStorage.removeItem(STORAGE_FLAGS.wasLoggedIn);
          return Promise.reject(error);
        }

        return Promise.reject(
          refreshError instanceof AxiosError
            ? refreshError
            : new Error('Nie udało się odświeżyć tokenu. Zaloguj się ponownie.'),
        );
      }
    }
    return Promise.reject(error);
  },
);

export default API;
