import useGetUserExercises from '@/hooks/query/exercises/use-get-user-exercises';
import ExerciseList from '../exercise-list';
import usePaginationQuery from '@/hooks/use-pagination-query';
import type { WorkoutSheetTabProps } from '@/types/workout';

const UserExercisesTab = ({ onExerciseToggle, isExerciseAdded }: WorkoutSheetTabProps) => {
  const { params, setPage } = usePaginationQuery();
  const {
    data: exercises,
    isLoading,
    isError,
  } = useGetUserExercises(params, { ownExercises: true });

  return (
    <ExerciseList
      exercises={exercises}
      isError={isError}
      isExerciseAdded={isExerciseAdded}
      isLoading={isLoading}
      listType="own"
      onExerciseToggle={onExerciseToggle}
      params={params}
      setPage={setPage}
    />
  );
};

export default UserExercisesTab;
