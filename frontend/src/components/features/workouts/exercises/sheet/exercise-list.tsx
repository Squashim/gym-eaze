import ReusablePagination from '@/components/ui/reusable-pagination';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';
import { parseExerciseLabel } from '@/lib/parsers';
import { cn } from '@/lib/utils';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { ExerciseFilters, FilteredQueryParams, PaginatedQueryParams } from '@/types/filters';
import { useMemo, useRef } from 'react';
import ExerciseItem from './exercise-item';

interface ExerciseListProps {
  params: FilteredQueryParams<ExerciseFilters> | PaginatedQueryParams;
  listType: 'social' | 'own' | 'favorite';
  setPage: (page: number) => void;
  isExerciseAdded: (id: string) => boolean;
  onExerciseToggle: (id: string) => void;
  exercises?: {
    items: GetExercisesApiResponse[];
    total: number;
    pageSize: number;
  };
  isError?: boolean;
  isLoading: boolean;
}

const ExerciseList = ({
  isLoading,
  exercises,
  params,
  isError,
  setPage,
  onExerciseToggle,
  isExerciseAdded,
  listType,
}: ExerciseListProps) => {
  const scrollRef = useRef<HTMLHeadingElement>(null);
  const totalPages = useMemo(() => {
    return exercises ? Math.ceil(exercises.total / exercises.pageSize) : 0;
  }, [exercises]);

  if (isLoading) {
    return <ExerciseItemSkeleton />;
  }

  if (isError) {
    return <ExerciseErrorState isError={isError} />;
  }

  return (
    <section className="px-4 pb-4 gap-4 flex flex-col">
      <div className="grid gap-3 h-full">
        <Typography className="text-lg" ref={scrollRef} variant="h4">
          Znaleziono {parseExerciseLabel(exercises?.total ?? 0)}
        </Typography>
        <div className="overflow-y-auto scrollbar flex flex-col gap-3 max-h-[63dvh]">
          {exercises && exercises.items.length === 0 ? (
            <ExerciseErrorState listType={listType} />
          ) : (
            exercises?.items.map((item) => (
              <ExerciseItem
                isExerciseAdded={isExerciseAdded}
                item={item}
                key={item._id}
                onExerciseToggle={onExerciseToggle}
              />
            ))
          )}
        </div>
      </div>

      <ReusablePagination
        currentPage={params.page}
        onPageChange={setPage}
        scrollToRef={scrollRef}
        totalPages={totalPages}
      />
    </section>
  );
};

const ExerciseItemSkeleton = () => {
  return (
    <div className="px-4 pb-4 grid gap-3">
      <Skeleton className="h-9 w-1/3 rounded-md" />
      <div className="overflow-y-auto scrollbar flex flex-col gap-3 max-h-[63dvh] h-full">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton className="min-h-26 rounded-md" key={i} />
        ))}
      </div>
      <Skeleton className="h-12 w-1/2 rounded-md mx-auto" />
    </div>
  );
};

interface ExerciseErrorStateProps {
  isError?: boolean;
  listType?: 'social' | 'own' | 'favorite';
}

const ExerciseErrorState = ({ isError, listType }: ExerciseErrorStateProps) => {
  const getMessage = () => {
    switch (listType) {
      case 'own':
        return {
          title: 'Nie masz jeszcze żadnego ćwiczenia!',
        };
      case 'favorite':
        return {
          title: 'Brak ulubionych ćwiczeń!',
        };
      case 'social':
        return {
          title: 'Nic nie znaleziono! Spróbuj zmienić lub usunąć niektóre filtry.',
        };
    }
  };
  return (
    <div
      className={cn(
        'rounded-md p-3 shadow-md flex flex-col justify-center items-center gap-2 border h-26 bg-card text-card-foreground',
        isError && 'mx-4',
      )}
    >
      <Typography className="text-center" variant="h4">
        {isError && 'Błąd podczas pobierania ćwiczeń. Spróbuj ponownie później.'}
        {listType && getMessage()?.title}
      </Typography>
    </div>
  );
};

export default ExerciseList;
