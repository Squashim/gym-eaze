import useGetUserWorkouts from '@/hooks/query/workouts/use-get-user-workouts';
import WorkoutContent from '../../workouts/workout-content';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';

interface WorkoutsTabProps {
  isOwnProfile: boolean;
  profileId: string;
}

function WorkoutsTab({ profileId, isOwnProfile }: WorkoutsTabProps) {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const {
    data: workouts,
    isLoading,
    isError,
  } = useGetUserWorkouts(params, {
    ownExercises: isOwnProfile,
    userId: isOwnProfile ? '' : profileId,
  });

  return (
    <div className="flex flex-col gap-4 w-full h-full py-4">
      <WorkoutContent
        className="sm:grid-cols-1 lg:grid-cols-2"
        isError={isError}
        isLoading={isLoading}
        pageType={isOwnProfile ? 'user' : 'profile'}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
        workouts={workouts}
      />
    </div>
  );
}

export default WorkoutsTab;
