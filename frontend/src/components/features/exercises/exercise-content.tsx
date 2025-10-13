import ReusablePagination from '@/components/ui/reusable-pagination';
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
import ItemsSkeleton from '@/components/ui/items-skeleton';
import { cn } from '@/lib/utils';

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
  className?: string;
  isLoading: boolean;
  isError: boolean;
};

const ExerciseContent = ({
  exercises,
  isLoading,
  isError,
  pagination,
  className,
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
      case 'profile':
        return `Ćwiczenia użytkownika: ${exercises?.total ?? 0}`;
      default:
        return `Znaleziono ${exercises?.total ?? 0} ćwiczeń`;
    }
  };

  if (isLoading) {
    return <ItemsSkeleton />;
  }

  return (
    <>
      <section
        className={cn(
          'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 h-full justify-center',
          className,
        )}
      >
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

export default ExerciseContent;
