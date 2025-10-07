import WorkoutContent from '@/components/features/workouts/workout-content';
import PageLayout from '@/components/layout/page';
import { initialWorkoutFilters } from '@/constants/filters';
import useGetWorkouts from '@/hooks/query/workouts/use-get-workouts';
import useFilterQuery from '@/hooks/use-filter-query';
import usePageTitle from '@/hooks/utils/use-page-title';
import type { WorkoutFilters } from '@/types/filters';

function WorkoutBrowser() {
  const { params, setPage, setFilters, setPageSize, setSortBy, clearFilters } =
    useFilterQuery<WorkoutFilters>(initialWorkoutFilters);
  const { data: workouts, isLoading, isError } = useGetWorkouts(params);

  usePageTitle('Przeglądaj plany');

  return (
    <PageLayout
      description="Przeglądaj plany treningowe udostępniane przez innych użytkowników. Możesz użyć filtrów, aby znaleźć te, które najlepiej pasują do Twoich potrzeb."
      title="Przeglądaj plany treningowe"
    >
      <WorkoutContent
        isError={isError}
        workouts={workouts}
        isLoading={isLoading}
      // pagination={{
      //   params,
      //   setPage,
      //   setPageSize,
      // }}
      />
    </PageLayout>
  );
}

export default WorkoutBrowser;
