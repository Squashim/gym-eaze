import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import useGetUserExercises from '@/hooks/query/exercises/use-get-user-exercises';
import ExerciseContent from '../../exercises/exercise-content';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';

interface ExercisesTabProps {
  isOwnProfile: boolean;
  profileId: string;
  isViewingAsGuest: boolean;
}

function ExercisesTab({ profileId, isOwnProfile, isViewingAsGuest }: ExercisesTabProps) {
  const { params, setPage, setPageSize } = usePaginationQuery();

  const {
    data: exercises,
    isLoading,
    isError,
  } = useGetUserExercises(profileId, params, isOwnProfile && !isViewingAsGuest);

  return (
    <div className="flex flex-col gap-4 w-full h-full py-4">
      <ExerciseContent
        className="sm:grid-cols-1 lg:grid-cols-2"
        exercises={exercises}
        isError={isError}
        isLoading={isLoading}
        pageType={isOwnProfile && !isViewingAsGuest ? 'user' : 'profile'}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
      <ScrollToTopButton />
    </div>
  );
}

export default ExercisesTab;
