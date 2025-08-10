import type { ApiResponse } from '@/types/api';
import type { AxiosError } from 'axios';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getApiErrorMsg(error: unknown): string | undefined {
  if (typeof error === 'object' && error !== null && (error as AxiosError).isAxiosError) {
    const axiosError = error as AxiosError<ApiResponse>;

    if (
      axiosError.response &&
      axiosError.response.data &&
      typeof axiosError.response.data.message === 'string'
    ) {
      return axiosError.response.data.message;
    }
  }

  return undefined;
}
