import { getByUser } from '@/api/exercise';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import { useQuery } from '@tanstack/react-query';

type UseGetUserExercisesProps = { ownExercises: true } | { ownExercises?: false; userId: string };

function useGetUserExercises(params: PaginatedQueryParams, options: UseGetUserExercisesProps) {
  const { user } = useUser();
  const targetUserId = options.ownExercises ? user!._id : options.userId;

  return useQuery<PaginatedResponse<GetExercisesApiResponse>>({
    queryKey: QUERY_KEYS.USER_EXERCISES(targetUserId ?? '', params),
    queryFn: () => getByUser(targetUserId, params),
    retry: false,
    enabled: !!targetUserId,
  });
}

export default useGetUserExercises;
