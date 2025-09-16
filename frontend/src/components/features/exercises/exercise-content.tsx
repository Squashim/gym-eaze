import ReusablePagination from '@/components/ui/reusable-pagination';
import { Skeleton } from '@/components/ui/skeleton';
import ExerciseList from './exercise-list';
import Typography from '@/components/ui/typography';
import { useMemo, useRef } from 'react';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type {
  ExerciseFilters,
  FilteredQueryParams,
  PageSize,
  PageType,
  PaginatedQueryParams,
} from '@/types/filters';

type ExerciseContentProps = {
  pagination: {
    params: PaginatedQueryParams | FilteredQueryParams<ExerciseFilters>;
    setPage: (page: number) => void;
    setPageSize: (pageSize: PageSize) => void;
  };
  exercises?: {
    items: GetExercisesApiResponse[];
    total: number;
    pageSize: number;
  };
  pageType?: PageType;
  isLoading: boolean;
  isError: boolean;
};

const ExerciseContent = ({
  exercises,
  isLoading,
  isError,
  pagination,
  pageType = 'browser',
}: ExerciseContentProps) => {
  const scrollRef = useRef<HTMLHeadingElement>(null);
  const totalPages = useMemo(() => {
    return exercises ? Math.ceil(exercises.total / exercises.pageSize) : 0;
  }, [exercises]);

  const renderSubtitle = () => {
    switch (pageType) {
      case 'user':
        return `Stworzone ćwiczenia: ${exercises?.total ?? 0}`;
      case 'favorite':
        return `Dodane do ulubionych: ${exercises?.total ?? 0}`;
      default:
        return `Znaleziono ${exercises?.total ?? 0} ćwiczeń`;
    }
  };

  if (isLoading) {
    return <ExercisesSkeleton />;
  }

  return (
    <>
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 h-full justify-center">
        <Typography className="col-span-full" ref={scrollRef} variant="h4">
          {renderSubtitle()}
        </Typography>
        <ExerciseList exercises={exercises?.items} isFetchingError={isError} pageType={pageType} />
      </section>

      <ReusablePagination
        currentPage={pagination.params.page}
        onPageChange={pagination.setPage}
        onPageSizeChange={pagination.setPageSize}
        pageSize={pagination.params.pageSize}
        scrollToRef={scrollRef}
        totalPages={totalPages}
      />
    </>
  );
};

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

export default ExerciseContent;
