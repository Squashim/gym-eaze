import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { AdvancedLevelBadge } from '@/components/ui/item-info-badges';
import ItemsSkeleton from '@/components/ui/items-skeleton';
import ReusablePagination from '@/components/ui/reusable-pagination';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Goal } from '@/constants/enums/goals';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import useGetActiveWorkouts from '@/hooks/query/active-workouts/use-get-active-workouts';
import { parseWorkoutDaysLabel, parseWorkoutDurationLabel } from '@/lib/parsers';
import { cn } from '@/lib/utils';
import type { GetActiveWorkoutsApiResponse } from '@/types/active-workout';
import type { PageSize, PaginatedQueryParams } from '@/types/filters';
import { Info } from 'lucide-react';
import { useMemo, useRef } from 'react';

interface ActiveWorkoutsTabProps {
  isOwnProfile: boolean;
  profileId: string;
  isViewingAsGuest: boolean;
}

function ActiveWorkoutsTab({ isOwnProfile, profileId, isViewingAsGuest }: ActiveWorkoutsTabProps) {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const {
    data: activeWorkouts,
    isLoading,
    isError,
  } = useGetActiveWorkouts(profileId, params, isOwnProfile && !isViewingAsGuest);

  return (
    <div className="flex flex-col gap-4 w-full h-full pb-4">
      <ActiveWorkoutContent
        activeWorkouts={activeWorkouts}
        isError={isError}
        isLoading={isLoading}
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
      <ScrollToTopButton />
    </div>
  );
}

interface ActiveWorkoutContentProps {
  activeWorkouts?: {
    items: GetActiveWorkoutsApiResponse[];
    total: number;
    pageSize: number;
  };
  pagination: {
    params: PaginatedQueryParams;
    setPage: (page: number) => void;
    setPageSize: (pageSize: PageSize) => void;
  };
  isLoading: boolean;
  isError: boolean;
}

function ActiveWorkoutContent({
  activeWorkouts,
  isLoading,
  isError,
  pagination,
}: ActiveWorkoutContentProps) {
  const scrollRef = useRef<HTMLHeadingElement>(null);
  const totalPages = useMemo(() => {
    return activeWorkouts ? Math.ceil(activeWorkouts.total / activeWorkouts.pageSize) : 0;
  }, [activeWorkouts]);

  if (isLoading) {
    return <ItemsSkeleton />;
  }

  return (
    <>
      <section className="grid grid-cols-1 gap-4 justify-center sm:grid-cols-1 lg:grid-cols-2">
        <Alert className="col-span-full border-primary/70">
          <Info />
          <AlertTitle className="line-clamp-none">
            Zarządzanie aktywnymi planami treningowymi dostępne w aplikacji mobilnej!
          </AlertTitle>
          <AlertDescription>
            Aktywne plany synchronizują się z aplikacją mobilną, gdzie możesz rozpoczynać treningi,
            śledzić postępy i oznaczać ukończone dni. Pobierz aplikację, aby w pełni wykorzystać
            funkcje treningowe.
          </AlertDescription>
        </Alert>

        <Typography className="col-span-full" ref={scrollRef} variant="h4">
          {`Aktywne plany treningowe (${activeWorkouts?.total ?? 0})`}
        </Typography>
        <ActiveWorkoutList activeWorkouts={activeWorkouts?.items} isFetchingError={isError} />
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

interface ActiveWorkoutListProps {
  isFetchingError: boolean;
  activeWorkouts?: GetActiveWorkoutsApiResponse[];
}

function ActiveWorkoutList({ isFetchingError, activeWorkouts }: ActiveWorkoutListProps) {
  if (isFetchingError) {
    return (
      <Alert className="col-span-full">
        <Info />
        <AlertTitle className="line-clamp-none">
          Wystąpił błąd podczas pobierania aktywnych planów
        </AlertTitle>
        <AlertDescription>Spróbuj odświeżyć stronę lub wrócić później.</AlertDescription>
      </Alert>
    );
  }

  if (!activeWorkouts || activeWorkouts.length === 0) {
    return (
      <Alert className="col-span-full">
        <Info />
        <AlertTitle className="line-clamp-none">Brak aktywnych planów treningowych</AlertTitle>
        <AlertDescription>Użytkownik nie rozpoczął żadnego planu treningowego.</AlertDescription>
      </Alert>
    );
  }

  return activeWorkouts.map((workout) => <ActiveWorkoutCard key={workout._id} workout={workout} />);
}

function ActiveWorkoutCard({ workout }: { workout: GetActiveWorkoutsApiResponse }) {
  return (
    <a
      className="rounded-xl outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] max-w-md mx-auto w-full"
      href={`/workouts/${workout.originalId}`}
      rel="noreferrer"
      target="_blank"
      title="Przejdź do strony planu treningowego"
    >
      <Card className="group hover:shadow-xl hover:shadow-primary/40 hover:border-primary hover:bg-gradient-to-br hover:from-card hover:to-primary/5 transition-all duration-300 ease-in-out h-full transform hover:-translate-y-1 gap-4">
        <CardHeader className="gap-2 ">
          {workout.advancedLevel !== AdvancedLevel.none && (
            <AdvancedLevelBadge advancedLevel={workout.advancedLevel} />
          )}

          <div className="flex w-full aspect-[4/3] rounded-xl overflow-hidden relative">
            <img
              alt={`zdjęcie poglądowe ćwiczenia ${workout.name}`}
              className="w-full h-full object-cover rounded-xl transition-transform duration-500 ease-out group-hover:scale-105 dark:grayscale-[0.2] dark:contrast-110 bg-primary/5"
              src={workout.image}
            />
          </div>

          <CardTitle className="text-xl line-clamp-2">{workout.name}</CardTitle>
          <CardDescription>{`${parseWorkoutDurationLabel(workout.totalDays)}`}</CardDescription>
          <CardContent className="p-0 flex flex-col gap-3">
            <ActiveWorkoutStatusBar
              completedDays={workout.completedDays}
              totalDays={workout.totalDays}
            />
            <div className="flex flex-wrap gap-2 justify-between items-center">
              {workout.goal !== Goal.none && <Badge variant="accent">{workout.goal}</Badge>}

              <div className="flex gap-2 items-center flex-wrap ml-auto">
                <Badge variant="accent">
                  {parseWorkoutDaysLabel(workout.trainingDays, 'training')}
                </Badge>
                {workout.restDays > 0 && (
                  <Badge className="text-secondary" variant="accent">
                    {parseWorkoutDaysLabel(workout.restDays, 'rest')}
                  </Badge>
                )}
              </div>
            </div>
          </CardContent>
        </CardHeader>
        <CardFooter className="mt-auto ml-auto text-sm text-muted-foreground">
          Stworzone przdez: {workout.author.username}
        </CardFooter>
      </Card>
    </a>
  );
}

interface ActiveWorkoutStatusBarProps {
  totalDays: number;
  completedDays: number;
}

function ActiveWorkoutStatusBar({ totalDays, completedDays }: ActiveWorkoutStatusBarProps) {
  const isFinished = completedDays === totalDays;

  return (
    <div className="flex flex-col gap-2 my-2">
      <Typography variant="small">
        {isFinished ? 'Ukończony plan treningowy!' : `Ukończono ${completedDays}/${totalDays} dni`}
      </Typography>
      <div className="w-full bg-primary/20 border h-3 rounded-full overflow-hidden">
        <div
          className={cn(
            'bg-primary h-full rounded-full transition-all duration-500 ease-in-out',
            isFinished && 'bg-secondary',
          )}
          style={{ width: `${(completedDays / totalDays) * 100}%` }}
        ></div>
      </div>
    </div>
  );
}

export default ActiveWorkoutsTab;
