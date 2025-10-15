import WorkoutContent from '@/components/features/workouts/workout-content';
import WorkoutFilters from '@/components/features/workouts/workout-filters';
import PageLayout from '@/components/layout/page';
import { defaultWorkoutFilters } from '@/constants/defaults/filters';
import useGetWorkouts from '@/hooks/query/workouts/use-get-workouts';
import useFilterQuery from '@/hooks/filters-pagination/use-filter-query';
import usePageTitle from '@/hooks/utils/use-page-title';
import type { WorkoutFilters as WorkoutFiltersType } from '@/types/filters';

function WorkoutBrowser() {
  const { params, setPage, setFilters, setPageSize, setSortBy, clearFilters } =
    useFilterQuery<WorkoutFiltersType>(defaultWorkoutFilters);
  const { data: workouts, isLoading, isError } = useGetWorkouts(params);

  usePageTitle('Przeglądaj plany');

  return (
    <PageLayout
      description="Przeglądaj plany treningowe udostępniane przez innych użytkowników. Możesz użyć filtrów, aby znaleźć te, które najlepiej pasują do Twoich potrzeb."
      title="Przeglądaj plany treningowe"
    >
      <WorkoutFilters
        clearFilters={clearFilters}
        filters={params.filters ?? {}}
        setFilters={setFilters}
        setSortBy={setSortBy}
        sortBy={params.sortBy}
      />
      <WorkoutContent
        isError={isError}
        isLoading={isLoading}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
        workouts={workouts}
      />
    </PageLayout>
  );
}

export default WorkoutBrowser;
