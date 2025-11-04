import { WORKOUT } from '@/constants/workout';
import type { TrainingDaySchema } from '@/schemas/workout-schema';
import type { ApiResponse, GoogleLoginConflictResponse } from '@/types/api';
import type { Equipment } from '@/types/equipment';
import type { MuscleGroup } from '@/types/muscle-group';
import type { DurationSet, RepsSet, WorkoutApiDay, WorkoutApiExercise } from '@/types/workout';
import { AxiosError, isAxiosError } from 'axios';
import { clsx, type ClassValue } from 'clsx';
import type { FieldValues } from 'react-hook-form';
import { twMerge } from 'tailwind-merge';
import { parseTimeBrackets } from './parsers';
import { Country } from '@/constants/enums/countries';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';

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

export function apiResponseToMultiSelectOptions(
  items: Equipment[] | MuscleGroup[] | undefined,
): { label: string; value: string; image?: string }[] {
  if (!items) return [];

  return items.map((item) => ({
    label: item.name,
    value: item._id,
    image: item.image,
  }));
}

export function objectToFormData<
  T extends Record<string, string | File | string[] | undefined | null>,
>(obj: T): FormData {
  const formData = new FormData();

  for (const key in obj) {
    if (!Object.hasOwn(obj, key)) continue;

    const value = obj[key];

    if (value === undefined || value === null) continue;

    if (Array.isArray(value)) {
      formData.append(key, JSON.stringify(value));
    } else if (value instanceof File) {
      formData.append(key, value);
    } else {
      formData.append(key, value);
    }
  }

  return formData;
}

export function extractDirtyFieldsData<
  TFormData extends FieldValues,
  TPayload extends Record<string, unknown>,
>(dirtyFields: Partial<Record<keyof TFormData, unknown>>, formData: TFormData): TPayload {
  const formattedData = {} as TPayload;

  (Object.keys(dirtyFields) as (keyof TFormData)[]).forEach((key) => {
    const dirty = dirtyFields[key];

    if (dirty === true) {
      const value = formData[key];
      if (value !== undefined) {
        (formattedData as Record<string, unknown>)[key as string] = value;
      }
      return;
    }

    if (dirty && typeof dirty === 'object' && dirty !== null) {
      const nestedData = formData[key];
      Object.keys(dirty).forEach((nestedKey) => {
        if ((dirty as Record<string, unknown>)[nestedKey]) {
          if (nestedData && typeof nestedData === 'object' && nestedKey in nestedData) {
            const value = (nestedData as Record<string, unknown>)[nestedKey];
            if (value !== undefined) {
              (formattedData as Record<string, unknown>)[nestedKey] = value;
            }
          }
        }
      });
    }
  });

  return formattedData;
}

export function areFilesEqual(file1: File | null, file2: File | null): boolean {
  if (file1 === null && file2 === null) return true;
  if (file1 === null || file2 === null) return false;
  return file1.name === file2.name && file1.size === file2.size && file1.type === file2.type;
}

function getExtensionMimeType(ext: string | undefined): string {
  switch (ext) {
    case 'jpg':
    case 'jpeg':
      return 'image/jpeg';
    case 'png':
      return 'image/png';
    case 'webp':
      return 'image/webp';
    case 'mp4':
      return 'video/mp4';
    case 'webm':
      return 'video/webm';
    default:
      return 'application/octet-stream';
  }
}

function getFileInfoFromUrl(url: string): { filename: string; mimeType: string } {
  const pathname = new URL(url).pathname;
  let filename = pathname.split('/').pop() || 'plik';
  const haveParts = filename.includes('_');
  const ext = filename.split('.').pop()?.toLowerCase() || '';

  if (haveParts) {
    const parts = filename.split('_');
    const baseName = parts.shift() || 'plik';
    filename = `${baseName}.${ext}`;
  }
  const mimeType = getExtensionMimeType(ext);
  return { filename, mimeType };
}

export async function urlToFile(url: string): Promise<File> {
  const { filename, mimeType } = getFileInfoFromUrl(url);
  const res = await fetch(url);
  const blob = await res.blob();
  return new File([blob], filename, { type: mimeType || blob.type });
}

export function serializeQuery(params: Record<string, unknown>): string {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((v) => {
        if (typeof v === 'string' || typeof v === 'number' || typeof v === 'boolean') {
          searchParams.append(key, String(v));
        }
      });
    } else if (
      typeof value === 'string' ||
      typeof value === 'number' ||
      typeof value === 'boolean'
    ) {
      searchParams.append(key, String(value));
    }
  });
  return searchParams.toString();
}

export function getWeeks<T extends TrainingDaySchema | WorkoutApiDay>(days: T[]) {
  return Array.from({ length: Math.ceil(days.length / 7) }, (_, weekIndex) => {
    const start = weekIndex * 7;
    const end = Math.min(start + 7, days.length);
    return {
      name: `Tydzień ${weekIndex + 1}`,
      days: days.slice(start, end),
    };
  });
}

export function getWorkoutDefaultSets(isRepsSet: true): RepsSet[];
export function getWorkoutDefaultSets(isRepsSet: false): DurationSet[];
export function getWorkoutDefaultSets(isRepsSet: boolean): RepsSet[] | DurationSet[] {
  if (isRepsSet) {
    return Array.from({ length: WORKOUT.DEFAULT_SETS }, () => ({
      repetitions: WORKOUT.DEFAULT_SETS_REPS,
      weight: null,
    }));
  } else {
    return Array.from({ length: WORKOUT.DEFAULT_SETS }, () => ({
      duration: WORKOUT.DEFAULT_SETS_DURATION,
    }));
  }
}

export function calculateWorkoutTime<T extends TrainingDaySchema | WorkoutApiDay>(day: T): number {
  let totalSeconds = 0;
  day.exercises.forEach((ex, idx) => {
    const setsCount = ex.sets.length;
    const rest = ex.restAfterSet;
    const includeLastRest = idx === day.exercises.length - 1;

    if (ex.isRepsSet) {
      totalSeconds += ex.sets.reduce(
        (sum, set) => sum + set.repetitions * WORKOUT.DEFAULT_SETS_REP_DURATION,
        0,
      );
    } else {
      totalSeconds += ex.sets.reduce((sum, set) => sum + set.duration, 0);
    }

    const restAfterSetCount = includeLastRest ? setsCount : Math.max(0, setsCount - 1);
    totalSeconds += restAfterSetCount * rest;
  });

  return totalSeconds;
}

export function getDayTimeRange<T extends TrainingDaySchema | WorkoutApiDay>(day: T): string {
  const totalSeconds = calculateWorkoutTime(day);
  return parseTimeBrackets(totalSeconds);
}

export function getCountryCodeByName(name: string): keyof typeof Country {
  return Object.entries(Country).find(
    ([_, countryName]) => countryName === name,
  )?.[0] as keyof typeof Country;
}

export function calculateBMI(weight: number, height: number): number {
  const heightInMeters = height / 100;
  const bmi = weight / (heightInMeters * heightInMeters);
  return Math.round(bmi * 10) / 10;
}

export function isProfilePostQuery(queryKey: unknown): boolean {
  return (
    Array.isArray(queryKey) &&
    queryKey.length === 3 &&
    queryKey[0] === 'user' &&
    queryKey[2] === 'posts'
  );
}

export function isReportsQuery(queryKey: unknown): boolean {
  return Array.isArray(queryKey) && queryKey.length === 2 && queryKey[0] === 'reports';
}

export function isItemReplyCommentsQuery(queryKey: unknown): boolean {
  return (
    Array.isArray(queryKey) &&
    queryKey.length === 3 &&
    queryKey[2] === 'comments' &&
    (queryKey[0] === 'posts' || queryKey[0] === 'exercises' || queryKey[0] === 'workouts')
  );
}

export const isDefaultExercise = (
  exercise: GetExercisesByIdsApiResponse | WorkoutApiExercise,
): exercise is WorkoutApiExercise => {
  return Boolean('originalId' in exercise);
};
