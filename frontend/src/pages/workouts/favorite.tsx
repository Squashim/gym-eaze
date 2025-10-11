import PageLayout from '@/components/layout/page';
import usePageTitle from '@/hooks/utils/use-page-title';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import WorkoutContent from '@/components/features/workouts/workout-content';
import useGetFavoriteWorkouts from '@/hooks/query/workouts/use-get-favorite-workouts';

const FavoriteWorkouts = () => {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const { data: workouts, isLoading, isError } = useGetFavoriteWorkouts(params);
  usePageTitle('Ulubione plany treningowe');

  return (
    <PageLayout
      description="To miejsce, w którym zebraliśmy Twoje ulubione plany. Przeglądaj je i zarządzaj nimi wygodnie."
      title="Ulubione plany treningowe"
    >
      <WorkoutContent
        isError={isError}
        isLoading={isLoading}
        pageType="favorite"
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
        workouts={workouts}
      />
    </PageLayout>
  );
};

export default FavoriteWorkouts;
