import useGetUserExercises from '@/hooks/query/exercises/use-get-user-exercises';
import ExerciseList from '../exercise-list';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import type { WorkoutSheetTabProps } from '@/types/workout';
import { useUser } from '@/context/user';

const UserExercisesTab = ({ onExerciseToggle, isExerciseAdded }: WorkoutSheetTabProps) => {
  const { params, setPage } = usePaginationQuery();
  const { user } = useUser();
  const { data: exercises, isLoading, isError } = useGetUserExercises(user!._id, params);

  return (
    <div className="flex flex-col h-full pb-3 max-h-[75dvh] sm:max-h-[calc(100dvh_-_11.5rem)] min-h-[30dvh]">
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
    </div>
  );
};

export default UserExercisesTab;
