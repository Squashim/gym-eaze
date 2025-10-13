import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import useGetUserExercises from '@/hooks/query/exercises/use-get-user-exercises';
import ExerciseContent from '../../exercises/exercise-content';

interface ExercisesTabProps {
  isOwnProfile: boolean;
  profileId: string;
}

function ExercisesTab({ profileId, isOwnProfile }: ExercisesTabProps) {
  const { params, setPage, setPageSize } = usePaginationQuery();

  const {
    data: exercises,
    isLoading,
    isError,
  } = useGetUserExercises(params, {
    ownExercises: isOwnProfile,
    userId: isOwnProfile ? '' : profileId,
  });

  return (
    <div className="flex flex-col gap-4 w-full h-full py-4">
      <ExerciseContent
        className="sm:grid-cols-1 lg:grid-cols-2"
        exercises={exercises}
        isError={isError}
        isLoading={isLoading}
        pageType={isOwnProfile ? 'user' : 'profile'}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
    </div>
  );
}

export default ExercisesTab;
