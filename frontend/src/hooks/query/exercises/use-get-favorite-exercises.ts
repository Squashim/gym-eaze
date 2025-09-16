import { getFavorite } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/query-keys';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

function useGetFavoriteExercises(params: PaginatedQueryParams) {
  return useQuery<PaginatedResponse<GetExercisesApiResponse>>({
    queryKey: QUERY_KEYS.FAVORITE_EXERCISES(params),
    queryFn: () => getFavorite(params),
    retry: false,
  });
}

export default useGetFavoriteExercises;
