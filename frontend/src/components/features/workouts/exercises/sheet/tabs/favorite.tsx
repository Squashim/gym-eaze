import useGetFavoriteExercises from '@/hooks/query/exercises/use-get-favorite-exercises';
import usePaginationQuery from '@/hooks/use-pagination-query';
import type { WorkoutSheetTabProps } from '@/types/workout';
import ExerciseList from '../exercise-list';

const FavoriteExercisesTab = ({ onExerciseToggle, isExerciseAdded }: WorkoutSheetTabProps) => {
  const { params, setPage } = usePaginationQuery();
  const { data: exercises, isLoading, isError } = useGetFavoriteExercises(params);

  return (
    <ExerciseList
      exercises={exercises}
      isError={isError}
      isExerciseAdded={isExerciseAdded}
      isLoading={isLoading}
      listType="favorite"
      onExerciseToggle={onExerciseToggle}
      params={params}
      setPage={setPage}
    />
  );
};

export default FavoriteExercisesTab;
