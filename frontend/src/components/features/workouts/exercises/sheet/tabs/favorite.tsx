import useGetFavoriteExercises from '@/hooks/query/exercises/use-get-favorite-exercises';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import type { WorkoutSheetTabProps } from '@/types/workout';
import ExerciseList from '../exercise-list';

const FavoriteExercisesTab = ({ onExerciseToggle, isExerciseAdded }: WorkoutSheetTabProps) => {
  const { params, setPage } = usePaginationQuery();
  const { data: exercises, isLoading, isError } = useGetFavoriteExercises(params);

  return (
    <div className="flex flex-col h-full pb-3 max-h-[75dvh] sm:max-h-[calc(100dvh_-_11.5rem)] min-h-[30dvh]">
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
    </div>
  );
};

export default FavoriteExercisesTab;
