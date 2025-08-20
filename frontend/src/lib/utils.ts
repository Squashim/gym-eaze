import type { ApiResponse, GoogleLoginConflictResponse } from '@/types/api';
import { AxiosError, isAxiosError } from 'axios';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getApiErrorMsg(error: unknown): string | undefined {
  if (isAxiosError(error)) {
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

export function isGoogleLoginConflictError(
  error: unknown,
): error is AxiosError<GoogleLoginConflictResponse> {
  if (isAxiosError(error)) {
    const axiosError = error as AxiosError<GoogleLoginConflictResponse>;

    if (
      axiosError.response &&
      axiosError.response.data &&
      axiosError.response.status === 409 &&
      typeof axiosError.response.data.email === 'string' &&
      typeof axiosError.response.data.linkRequired === 'boolean' &&
      typeof axiosError.response.data.token === 'string'
    ) {
      return true;
    }
  }
  return false;
}

export function extractGoogleLoginConflictData(
  error: unknown,
): Omit<GoogleLoginConflictResponse, 'linkRequired'> | null {
  if (isGoogleLoginConflictError(error) && error.response) {
    return {
      email: error.response.data.email,
      token: error.response.data.token,
    };
  }
  return null;
}

export function getSignupErrorInputField(error: string): string {
  switch (error) {
    case 'Użytkownik o takiej nazwie już istnieje':
      return 'username';
    case 'Użytkownik o takim emailu już istnieje':
    case 'Ten adres email jest już powiązany z kontem Google':
      return 'email';
    case 'Nieprawidłowe hasło.':
      return 'password';
    default:
      return 'root';
  }
}
