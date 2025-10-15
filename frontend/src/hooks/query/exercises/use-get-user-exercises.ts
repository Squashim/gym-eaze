import { getByUser } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

function useGetUserExercises(
  userId: string,
  params: PaginatedQueryParams,
  includePrivate: boolean = true,
) {
  return useQuery<PaginatedResponse<GetExercisesApiResponse>>({
    queryKey: QUERY_KEYS.USER_EXERCISES(userId, params, includePrivate),
    queryFn: () => getByUser(userId, params, includePrivate),
    retry: false,
    enabled: !!userId,
  });
}

export default useGetUserExercises;
