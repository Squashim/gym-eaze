import ReusablePagination from '@/components/ui/reusable-pagination';
import Typography from '@/components/ui/typography';
import type {
  FilteredQueryParams,
  PageSize,
  PageType,
  PaginatedQueryParams,
  WorkoutFilters,
} from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { useMemo, useRef } from 'react';
import WorkoutList from './workout-list';
import ItemsSkeleton from '@/components/ui/items-skeleton';
import { cn } from '@/lib/utils';

interface WorkoutContentProps {
  workouts?: {
    items: GetWorkoutsApiResponse[];
    total: number;
    pageSize: number;
  };
  pagination: {
    params: PaginatedQueryParams | FilteredQueryParams<WorkoutFilters>;
    setPage: (page: number) => void;
    setPageSize: (pageSize: PageSize) => void;
  };
  pageType?: PageType;
  className?: string;
  isLoading: boolean;
  isError: boolean;
}

function WorkoutContent({
  workouts,
  pageType = 'browser',
  isLoading,
  isError,
  className,
  pagination,
}: WorkoutContentProps) {
  const scrollRef = useRef<HTMLHeadingElement>(null);
  const totalPages = useMemo(() => {
    return workouts ? Math.ceil(workouts.total / workouts.pageSize) : 0;
  }, [workouts]);

  const renderSubtitle = () => {
    switch (pageType) {
      case 'user':
        return `Stworzone plany: ${workouts?.total ?? 0}`;
      case 'favorite':
        return `Dodane do ulubionych: ${workouts?.total ?? 0}`;
      case 'profile':
        return `Plany użytkownika: ${workouts?.total ?? 0}`;
      default:
        return `Znaleziono ${workouts?.total ?? 0} planów`;
    }
  };

  if (isLoading) {
    return <ItemsSkeleton />;
  }

  return (
    <>
      <section
        className={cn(
          'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 justify-center',
          className,
        )}
      >
        <Typography className="col-span-full" ref={scrollRef} variant="h4">
          {renderSubtitle()}
        </Typography>
        <WorkoutList isFetchingError={isError} pageType={pageType} workouts={workouts?.items} />
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
}

export default WorkoutContent;
