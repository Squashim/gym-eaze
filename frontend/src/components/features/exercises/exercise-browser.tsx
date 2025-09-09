import useGetExercises from '@/hooks/query/exercises/use-get-exercises';
import Typography from '@/components/ui/typography';
import type { Exercise } from '@/types/exercise';
import ReusablePagination from '@/components/ui/reusable-pagination';
import ExerciseFilters from './exercise-filters';
import { Skeleton } from '@/components/ui/skeleton';
import useFilterQuery from '@/hooks/use-filter-query';
import type { ExerciseFilters as ExerciseFiltersType } from '@/types/filters';
import { initialExerciseFilters } from '@/constants/filters';

const ExerciseBrowser = () => {
  const { params, setPage, setFilters, setPageSize, setSortBy, clearFilters } =
    useFilterQuery<ExerciseFiltersType>(initialExerciseFilters);
  const { data: exercises, isLoading } = useGetExercises(params);
  const totalPages = exercises ? Math.ceil(exercises.total / exercises.pageSize) : 0;

  return (
    <div className=" flex flex-col gap-6 my-6 w-full">
      <header className="grid gap-1">
        <Typography className="text-left" variant="h1">
          Przeglądaj ćwiczenia
        </Typography>
        <Typography className="text-left" variant="lead">
          Przeglądaj najnowsze ćwiczenia udostępniane przez innych użytkowników. Możesz filtrować
          ćwiczenia według różnych kryteriów, aby znaleźć te, które najlepiej pasują do Twoich
          potrzeb.
        </Typography>
      </header>

      <ExerciseFilters
        clearFilters={clearFilters}
        filters={params.filters ?? {}}
        setFilters={setFilters}
        setSortBy={setSortBy}
        sortBy={params.sortBy}
      />
      {isLoading ? (
        <div className="flex flex-col w-full gap-4">
          <div className="flex justify-end">
            <Skeleton className="h-10 w-1/6" />
          </div>
          <Skeleton className="h-10 w-1/3 mx-auto" />
        </div>
      ) : (
        <>
          {exercises!.items.map((ex) => (
            <ExerciseCard key={ex._id} {...ex} />
          ))}
          <ReusablePagination
            currentPage={params.page}
            onPageChange={setPage}
            onPageSizeChange={setPageSize}
            pageSize={params.pageSize}
            totalPages={totalPages}
          />
        </>
      )}
    </div>
  );
};

export default ExerciseBrowser;

const ExerciseCard = (exercise: Exercise) => {
  return (
    <div>
      <Typography variant="p">{exercise.name}</Typography>
    </div>
  );
};
