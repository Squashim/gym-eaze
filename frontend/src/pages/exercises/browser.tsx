import useGetExercises from '@/hooks/query/exercises/use-get-exercises';
import ExerciseFilters from '../../components/features/exercises/exercise-filters';
import useFilterQuery from '@/hooks/filters-pagination/use-filter-query';
import type { ExerciseFilters as ExerciseFiltersType } from '@/types/filters';
import { defaultExerciseFilters } from '@/constants/defaults/filters';
import usePageTitle from '@/hooks/utils/use-page-title';
import PageLayout from '@/components/layout/page';
import ExerciseContent from '../../components/features/exercises/exercise-content';

const ExerciseBrowser = () => {
  const { params, setPage, setFilters, setPageSize, setSortBy, clearFilters } =
    useFilterQuery<ExerciseFiltersType>(defaultExerciseFilters);
  const { data: exercises, isLoading, isError } = useGetExercises(params);

  usePageTitle('Przeglądaj ćwiczenia');

  return (
    <PageLayout
      description="Przeglądaj ćwiczenia udostępniane przez innych użytkowników. Możesz filtrować
          ćwiczenia według różnych kryteriów, aby znaleźć te, które najlepiej pasują do Twoich
          potrzeb."
      title="Przeglądaj ćwiczenia"
    >
      <ExerciseFilters
        clearFilters={clearFilters}
        filters={params.filters ?? {}}
        setFilters={setFilters}
        setSortBy={setSortBy}
        sortBy={params.sortBy}
      />
      <ExerciseContent
        exercises={exercises}
        isError={isError}
        isLoading={isLoading}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
    </PageLayout>
  );
};

export default ExerciseBrowser;
