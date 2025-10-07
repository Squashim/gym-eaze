import Typography from '@/components/ui/typography';
import type { PageType } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';

interface WorkoutContentProps {
  workouts?: {
    items: GetWorkoutsApiResponse[];
    total: number;
    pageSize: number;
  };
  pageType?: PageType;
  isLoading: boolean;
  isError: boolean;
}

function WorkoutContent({
  workouts,
  pageType = 'browser',
  isLoading,
  isError,
}: WorkoutContentProps) {
  return (
    <>
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 h-full justify-center">
        <Typography className="col-span-full" variant="h4">
          {JSON.stringify(workouts?.items)}
          {/* {renderSubtitle()} */}
        </Typography>
        {/* <ExerciseList exercises={exercises?.items} isFetchingError={isError} pageType={pageType} /> */}
      </section>

      {/* <ReusablePagination
        currentPage={pagination.params.page}
        onPageChange={pagination.setPage}
        onPageSizeChange={pagination.setPageSize}
        pageSize={pagination.params.pageSize}
        scrollToRef={scrollRef}
        totalPages={totalPages}
      /> */}
    </>
  );
}

export default WorkoutContent;
