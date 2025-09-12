import useGetExercises from '@/hooks/query/exercises/use-get-exercises';
import Typography from '@/components/ui/typography';
import ReusablePagination from '@/components/ui/reusable-pagination';
import ExerciseFilters from './exercise-filters';
import { Skeleton } from '@/components/ui/skeleton';
import useFilterQuery from '@/hooks/use-filter-query';
import type { ExerciseFilters as ExerciseFiltersType } from '@/types/filters';
import { initialExerciseFilters } from '@/constants/filters';
import { Card, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { AdvancedLevelBadge, LikesBadge } from '@/components/ui/item-info-badges';
import { AdvancedLevel } from '@/constants/advanced-level';
import usePageTitle from '@/hooks/use-page-title';
import { useRef } from 'react';
import type { GetExercisesApiResponse } from '@/types/exercise';

const ExerciseBrowser = () => {
  const { params, setPage, setFilters, setPageSize, setSortBy, clearFilters } =
    useFilterQuery<ExerciseFiltersType>(initialExerciseFilters);
  const { data: exercises, isLoading, isError } = useGetExercises(params);
  const scrollRef = useRef<HTMLHeadingElement>(null);
  const totalPages = exercises ? Math.ceil(exercises.total / exercises.pageSize) : 0;
  usePageTitle('Przeglądaj ćwiczenia');

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
        <ExercisesSkeleton />
      ) : (
        <>
          <main className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <Typography className="col-span-full" ref={scrollRef} variant="h4">
              Znaleziono {exercises?.total ?? 0} ćwiczeń
            </Typography>
            {!exercises || exercises.items.length === 0 ? (
              <EmptyExerciseCard isError={isError} />
            ) : (
              exercises.items.map((ex) => <ExerciseCard key={ex._id} {...ex} />)
            )}
          </main>

          <ReusablePagination
            currentPage={params.page}
            onPageChange={setPage}
            onPageSizeChange={setPageSize}
            pageSize={params.pageSize}
            scrollToRef={scrollRef}
            totalPages={totalPages}
          />
        </>
      )}
    </div>
  );
};

export default ExerciseBrowser;

const ExercisesSkeleton = () => {
  return (
    <div className="flex flex-col w-full gap-4">
      <main className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 place-items-center">
        <Skeleton className="col-span-full h-7 w-1/2 sm:w-1/3 lg:w-1/6 justify-self-start" />
        {Array.from({ length: 9 }).map((_, idx) => (
          <Skeleton className="h-105 w-full max-w-md rounded-xl" key={idx} />
        ))}
      </main>
      <div className="flex justify-end">
        <Skeleton className="h-10 w-1/6" />
      </div>
      <Skeleton className="h-10 w-1/3 mx-auto" />
    </div>
  );
};

const EmptyExerciseCard = ({ isError }: { isError: boolean }) => {
  return (
    <Card className="col-span-full min-h-36">
      <CardHeader>
        <div className="flex flex-col items-center justify-center text-center space-y-2">
          <CardTitle className="text-2xl">
            {isError ? 'Błąd podczas pobierania ćwiczeń' : 'Brak ćwiczeń do wyświetlenia'}
          </CardTitle>
          <CardDescription className="text-base text-muted-foreground max-w-sm">
            {isError
              ? 'Wystąpił błąd podczas pobierania ćwiczeń. Spróbuj odświeżyć stronę lub wrócić później.'
              : 'Nie znaleziono ćwiczeń spełniających podane kryteria. Spróbuj zmienić lub usunąć niektóre filtry.'}
          </CardDescription>
        </div>
      </CardHeader>
    </Card>
  );
};

const ExerciseCard = (exercise: GetExercisesApiResponse) => {
  const displayMuscleGroups = exercise.muscleGroup.map((mg) => mg.name).join(' • ');
  return (
    <a
      className="rounded-xl outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] max-w-md mx-auto w-full"
      href={`/exercises/${exercise._id}`}
      rel="noreferrer"
      target="_blank"
    >
      <Card className="group hover:shadow-xl hover:shadow-primary/40 hover:border-primary hover:bg-gradient-to-br hover:from-card hover:to-primary/5 transition-all duration-300 ease-in-out h-full transform hover:-translate-y-1">
        <CardHeader className="gap-2 ">
          <div className="flex justify-between items-center gap-3">
            <div>
              {exercise.advancedLevel !== AdvancedLevel.none && (
                <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
              )}
            </div>

            <LikesBadge likes={exercise.likes} />
          </div>

          <div className="flex w-full aspect-[4/3] rounded-xl overflow-hidden">
            <img
              alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
              className="w-full h-full object-cover rounded-xl transition-transform duration-500 ease-out group-hover:scale-105 dark:grayscale-[0.2] dark:contrast-110 bg-primary/5"
              src={exercise.image}
            />
          </div>

          <CardTitle className="text-xl line-clamp-2">{exercise.name}</CardTitle>
          <CardDescription>{displayMuscleGroups}</CardDescription>
        </CardHeader>
      </Card>
    </a>
  );
};
