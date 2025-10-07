import { getByUser } from '@/api/workout';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import type { PaginatedQueryParams, PaginatedResponse } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { useQuery } from '@tanstack/react-query';

type UseGetUserWorkoutsProps = { ownExercises: true } | { ownExercises?: false; userId: string };

function useGetUserWorkouts(params: PaginatedQueryParams, options: UseGetUserWorkoutsProps) {
  const { user } = useUser();
  const targetUserId = options.ownExercises ? user!._id : options.userId;

  return useQuery<PaginatedResponse<GetWorkoutsApiResponse>>({
    queryKey: QUERY_KEYS.USER_WORKOUTS(targetUserId ?? '', params),
    queryFn: () => getByUser(targetUserId, params),
    retry: false,
    enabled: !!targetUserId,
  });
}

export default useGetUserWorkouts;
