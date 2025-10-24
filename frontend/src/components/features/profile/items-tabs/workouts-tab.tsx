import useGetUserWorkouts from '@/hooks/query/workouts/use-get-user-workouts';
import WorkoutContent from '../../workouts/workout-content';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';

interface WorkoutsTabProps {
  isOwnProfile: boolean;
  profileId: string;
  isViewingAsGuest: boolean;
}

function WorkoutsTab({ profileId, isOwnProfile, isViewingAsGuest }: WorkoutsTabProps) {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const {
    data: workouts,
    isLoading,
    isError,
  } = useGetUserWorkouts(profileId, params, isOwnProfile && !isViewingAsGuest);

  return (
    <div className="flex flex-col gap-4 w-full h-full pb-4">
      <WorkoutContent
        className="sm:grid-cols-1 lg:grid-cols-2"
        isError={isError}
        isLoading={isLoading}
        isViewingAsGuest={isViewingAsGuest}
        pageType={isOwnProfile && !isViewingAsGuest ? 'user' : 'profile'}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
        workouts={workouts}
      />
      <ScrollToTopButton />
    </div>
  );
}

export default WorkoutsTab;
