import { ACTIVE_WORKOUT as endpoints } from '@/constants/api/endpoints';
import API from './api-client';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import type { GetActiveWorkoutsApiResponse } from '@/types/active-workout';

const getAll = async (
  userId: string,
  params: PaginatedQueryParams,
  includePrivate: boolean,
): Promise<PaginatedResponse<GetActiveWorkoutsApiResponse>> => {
  const { data } = await API.get<PaginatedResponse<GetActiveWorkoutsApiResponse>>(
    endpoints.base(userId),
    {
      params: {
        ...params,
        includePrivate: includePrivate ? 'true' : 'false',
      },
    },
  );

  return data;
};

export { getAll };
