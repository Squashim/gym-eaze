import BackHeader from '@/components/ui/back-header';
import WorkoutComments from '@/components/features/workouts/workout-comments';
import PageLayout from '@/components/layout/page';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import HashtagList from '@/components/ui/hashtag-list';
import { IconInfoField, InfoField } from '@/components/ui/info-fields';
import { AdvancedLevelBadge, LikesBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import ResourceActions from '@/components/ui/resource-actions';
import { DetailsActionBtn, DetailsAuthorBtn } from '@/components/ui/reusable-buttons';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Goal } from '@/constants/enums/goals';
import { useUser } from '@/context/user';
import useGetExercisesByIds from '@/hooks/query/exercises/use-get-exercises-by-ids';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import useGetWorkout from '@/hooks/query/workouts/use-get-workout';
import { useWorkout } from '@/hooks/query/workouts/use-workout';
import usePageTitle from '@/hooks/utils/use-page-title';
import {
  parseDayLabel,
  parseExerciseLabel,
  parseRepsLabel,
  parseSecondsToTimeString,
  parseTimeBrackets,
  parseWorkoutSetsLabel,
} from '@/lib/parsers';
import { cn, getApiErrorMsg, getDayTimeRange, getWeeks } from '@/lib/utils';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import type {
  DurationSet,
  GetWorkoutApiResponse,
  RepsSet,
  WorkoutDay,
  WorkoutExercise,
  WorkoutWeek,
} from '@/types/workout';
import { BicepsFlexed, CalendarDays, Clock, ImageOff, Info } from 'lucide-react';
import { useCallback, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import ReportDialog from '@/components/features/reports/report-dialog';
import useOpen from '@/hooks/utils/use-open';

function WorkoutDetails() {
  const { user } = useUser();
  const { id } = useParams();
  const reportDialog = useOpen();
  const { data: workout, isLoading, isError, error } = useGetWorkout(id!);

  const isAuthor = useMemo(() => {
    if (!user || !workout) return false;
    return user._id === workout.author._id;
  }, [user, workout]);

  const weeks = useMemo(() => {
    if (!workout) return [];
    return getWeeks(workout.days);
  }, [workout]);

  usePageTitle(workout?.name ?? 'Szczegóły planu');

  return (
    <PageLayout withFooter={false}>
      <WorkoutContent
        auth={{
          isAuthor,
          isLogged: !!user,
        }}
        error={error}
        isError={isError}
        isLoading={isLoading}
        reportDialog={{
          isOpen: reportDialog.isOpen,
          onOpenChange: reportDialog.setIsOpen,
          onClose: reportDialog.close,
          open: reportDialog.open,
        }}
        weeks={weeks}
        workout={workout}
      />
    </PageLayout>
  );
}

interface WorkoutContentProps {
  auth: { isAuthor: boolean; isLogged: boolean };
  workout?: GetWorkoutApiResponse;
  weeks: WorkoutWeek[];
  isLoading: boolean;
  isError: boolean;
  error: Error | null;
  reportDialog: {
    isOpen: boolean;
    onOpenChange: (isOpen: boolean) => void;
    onClose: () => void;
    open: () => void;
  };
}

function WorkoutContent({
  isLoading,
  workout,
  weeks,
  auth,
  error,
  isError,
  reportDialog,
}: WorkoutContentProps) {
  if (isLoading) {
    return <WorkoutSkeleton />;
  }

  if (isError || !workout) {
    return <EmptyWorkoutCard error={error} />;
  }

  return (
    <>
      <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
        <WorkoutHeader auth={auth} onReport={reportDialog.open} workout={workout} />

        <div className="w-full bg-accent/60 rounded-xl shadow-lg overflow-hidden h-[500px]">
          <img
            alt={`Zdjęcie planu treningowego ${workout.name}`}
            className="w-full rounded-xl h-full object-cover dark:grayscale-[0.2] dark:contrast-110"
            loading="lazy"
            src={workout.image}
          />
        </div>

        <div className="flex flex-col gap-4 min-w-0 h-full max-h-[500px]">
          <div className="flex justify-between items-center gap-3 flex-wrap">
            <div className="inline-flex gap-2">
              {auth.isAuthor && (
                <VisibilityBadge type="Workout" variant="accent" visibility={workout.visibility} />
              )}
              {workout.advancedLevel !== AdvancedLevel.none && (
                <AdvancedLevelBadge advancedLevel={workout.advancedLevel} />
              )}
            </div>

            <LikesBadge likes={workout.likes} />
          </div>

          <WorkoutDays weeks={weeks} />
        </div>

        <WorkoutMetaInfo isAuthor={auth.isAuthor} workout={workout} />

        <WorkoutComments workout={workout} />
      </section>

      {auth.isLogged && !auth.isAuthor && (
        <ReportDialog
          itemId={workout._id}
          itemType="Trening"
          onClose={reportDialog.onClose}
          onOpenChange={reportDialog.onOpenChange}
          open={reportDialog.isOpen}
        />
      )}
    </>
  );
}

const EmptyWorkoutCard = ({ error }: { error: Error | null }) => {
  return (
    <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
      <Skeleton className="col-span-full min-h-13 animate-none bg-card border text-card-foreground flex items-center p-6">
        <Typography variant="h1">Coś poszło nie tak...</Typography>
      </Skeleton>
      <Skeleton className="w-full aspect-square animate-none rounded-xl h-full bg-card border text-card-foreground flex items-center justify-center max-h-[600px]">
        <div className="size-124 rounded-full flex items-center justify-center">
          <ImageOff className="size-12 text-muted-foreground animate-pulse" />
        </div>
      </Skeleton>
      <div className="flex flex-col gap-4 min-w-0 h-full">
        <Skeleton className="w-full animate-none bg-card border text-card-foreground p-6 flex flex-col gap-4">
          <Typography variant="h2">
            {getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd'}
          </Typography>
          <Typography className="max-w-xl" variant="lead">
            Nie udało się wczytać planu. Spróbuj odświeżyć stronę lub wróć do listy planów
            treningowych.
          </Typography>
          <Button asChild className="w-full sm:w-fit" size="lg">
            <a href="/workouts/browse">Powrót do listy planów</a>
          </Button>
        </Skeleton>
        <Skeleton className="h-full w-full" />
      </div>
      <Skeleton className="h-17 w-full col-span-full" />
    </section>
  );
};

const WorkoutSkeleton = () => {
  return (
    <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
      <Skeleton className="h-13 w-3/4 md:w-full col-span-full" />
      <Skeleton className="aspect-square w-full rounded-xl max-h-[600px]" />
      <div className="flex flex-col gap-4 min-w-0 h-full">
        <Skeleton className="h-21 w-full" />
        <Skeleton className="h-full w-full" />
      </div>
      <Skeleton className="h-17 w-full col-span-full" />
    </section>
  );
};

interface WorkoutHeaderProps {
  workout: GetWorkoutApiResponse;
  auth: {
    isAuthor: boolean;
    isLogged: boolean;
  };
  onReport: () => void;
}

function WorkoutHeader({ workout, auth, onReport }: WorkoutHeaderProps) {
  const { deleteWorkout, toggleFavoriteWorkout, toggleLikeWorkout, downloadWorkout } = useWorkout();
  const { data: userLikes, isLoading: isLikedItemsLoading } = useGetLikedItems('trainingPlans');
  const { data: userFavorites, isLoading: isFavoritedItemsLoading } = useGetFavoritedItems(
    'trainingPlans',
    {
      enabled: !auth.isAuthor,
    },
  );

  const liked = useMemo(() => {
    if (!userLikes || !workout) return false;
    if (userLikes.has(workout?._id)) return true;
  }, [userLikes, workout]);

  const favorited = useMemo(() => {
    if (!userFavorites || !workout) return false;
    if (auth.isAuthor) return false;
    if (userFavorites.has(workout?._id)) return true;
  }, [userFavorites, workout, auth.isAuthor]);

  const handleLike = useCallback(() => {
    toggleLikeWorkout.mutate(workout._id);
  }, [toggleLikeWorkout, workout]);

  const handleFavorite = useCallback(() => {
    toggleFavoriteWorkout.mutate(workout._id);
  }, [toggleFavoriteWorkout, workout]);

  const handleDelete = useCallback(() => {
    deleteWorkout.mutate(workout._id);
  }, [deleteWorkout, workout]);

  const handleReport = useCallback(() => {
    onReport();
  }, [onReport]);

  const handleDownload = useCallback(() => {
    downloadWorkout.mutate(workout._id);
  }, [downloadWorkout, workout]);

  return (
    <header className="col-span-full flex flex-col md:flex-row md:items-center md:justify-between md:gap-4">
      <Typography className="text-left" variant="h1">
        {workout.name}
      </Typography>
      {auth.isLogged && (
        <ResourceActions
          className="mt-4 justify-end"
          delete={{
            onDelete: handleDelete,
            isDeleteLoading: deleteWorkout.isPending,
            deleteTitle: `Czy na pewno chcesz usunąć plan treningowy: ${workout.name}`,
            deleteDescription:
              'Ta akcja jest nieodwracalna. Wszystkie dane związane z tym planem zostaną trwale usunięte.',
          }}
          download={{ isDownloading: downloadWorkout.isPending, onDownload: handleDownload }}
          edit={{ redirectTo: `/workouts/${workout._id}/edit` }}
          favorite={{
            isFavorited: favorited || false,
            isFavoriteLoading: toggleFavoriteWorkout.isPending || isFavoritedItemsLoading,
            onFavorite: handleFavorite,
          }}
          isAuthor={auth.isAuthor}
          like={{
            isLiked: liked || false,
            isLikeLoading: toggleLikeWorkout.isPending || isLikedItemsLoading,
            onLike: handleLike,
          }}
          report={{ onReport: handleReport }}
        />
      )}
    </header>
  );
}

interface WorkoutDaysProps {
  weeks: WorkoutWeek[];
}

function WorkoutDays({ weeks }: WorkoutDaysProps) {
  const [selectedDay, setSelectedDay] = useState<WorkoutDay | null>(null);
  const [selectedTab, setSelectedTab] = useState<string>('week-1');

  const exerciseIds = useMemo(() => {
    if (!selectedDay) return null;
    return selectedDay.exercises.map((exercise) => exercise.id);
  }, [selectedDay]);

  const handleDaySelect = useCallback((day: WorkoutDay) => {
    setSelectedDay(day);
  }, []);

  const handleBack = useCallback(() => {
    setSelectedDay(null);
  }, []);

  if (weeks.length === 0) {
    return <Skeleton className="h-full" />;
  }

  if (selectedDay) {
    return (
      <WorkoutExercises exerciseIds={exerciseIds} onBack={handleBack} selectedDay={selectedDay} />
    );
  }

  return (
    <Tabs
      className="flex-1 min-h-0 max-h-full h-full"
      defaultValue={selectedTab}
      onValueChange={setSelectedTab}
    >
      <div className="relative overflow-x-auto h-16 pt-2 scrollbar">
        <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2 bg-transparent">
          {weeks.map((week, i) => (
            <TabsTrigger
              className="h-10 px-4 data-[state=active]:bg-card/70 dark:data-[state=active]:bg-card/70 bg-card/70 hover:border-input"
              key={week.name}
              value={`week-${i + 1}`}
            >
              {week.name}
            </TabsTrigger>
          ))}
        </TabsList>
      </div>

      <div className="overflow-y-auto scrollbar h-full p-0.5">
        {weeks.map((week, i) => (
          <TabsContent
            className="grid gap-3 focus-within:outline-none"
            key={i}
            value={`week-${i + 1}`}
          >
            {week.days.map((day) => (
              <DayItem day={day} key={day.dayNumber} onSelect={handleDaySelect} />
            ))}
          </TabsContent>
        ))}
      </div>
    </Tabs>
  );
}

interface DayItemProps {
  day: WorkoutDay;
  onSelect: (day: WorkoutDay) => void;
}

function DayItem({ day, onSelect }: DayItemProps) {
  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLDivElement>) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        onSelect(day);
      }
    },
    [day, onSelect],
  );

  return (
    <div
      className={cn(
        'rounded-md p-3 shadow-md flex flex-col border hover:bg-card/70 cursor-pointer transition-[background,border] ease-in-out duration-200 hover:border-primary/30 overflow-hidden bg-card',
        day.isRestDay && 'border-secondary pointer-events-none',
      )}
      onClick={() => onSelect(day)}
      onKeyDown={handleKeyDown}
      role="button"
      tabIndex={day.isRestDay ? -1 : 0}
      title="Kliknij, aby zobaczyć szczegóły dnia"
    >
      <Typography className="text-lg truncate max-w-full min-w-0">{day.name}</Typography>

      <Typography className={cn(day.isRestDay && 'text-secondary')} variant="muted">
        {day.isRestDay
          ? 'Regeneracja'
          : `${parseExerciseLabel(day.exercises.length)} • ${getDayTimeRange(day)}`}
      </Typography>
    </div>
  );
}

interface WorkoutExercisesProps {
  selectedDay: WorkoutDay;
  onBack: () => void;
  exerciseIds: string[] | null;
}

function WorkoutExercises({ selectedDay, onBack, exerciseIds }: WorkoutExercisesProps) {
  const { data: exercisesByIds, isLoading, isError } = useGetExercisesByIds(exerciseIds || []);

  return (
    <div className="flex flex-col flex-1 h-full min-h-0 max-h-[600px] overflow-hidden gap-3">
      <div className="sticky top-0 flex items-center justify-between gap-4 border flex-wrap bg-card rounded-md px-2 py-1">
        <BackHeader onBack={onBack} />
        <Typography className="font-semibold" variant="p">
          {selectedDay.name}
        </Typography>
      </div>

      <div className="overflow-y-auto scrollbar h-full flex flex-col gap-3">
        {isLoading ? (
          Array.from({ length: 6 }).map((_, index) => <Skeleton className="min-h-24" key={index} />)
        ) : isError ? (
          <Alert>
            <Info />
            <AlertTitle>Wystąpił błąd podczas pobierania ćwiczeń. Odśwież stronę.</AlertTitle>
          </Alert>
        ) : (
          exercisesByIds?.map((exercise) => {
            const dayExercise = selectedDay.exercises.find((fe) => fe.id === exercise._id);
            const restAfterSets = dayExercise ? dayExercise.restAfterSet : 0;

            return (
              <WorkoutExercise
                dayExercise={dayExercise}
                exercise={exercise}
                key={exercise._id}
                restAfterSets={restAfterSets}
              />
            );
          })
        )}
      </div>
    </div>
  );
}

interface WorkoutExerciseProps {
  exercise: GetExercisesByIdsApiResponse;
  restAfterSets: number;
  dayExercise: WorkoutExercise | undefined;
}

function WorkoutExercise({ exercise, restAfterSets, dayExercise }: WorkoutExerciseProps) {
  const displayMuscleGroups = exercise.muscleGroup.map((mg) => mg.name).join(' • ');

  const groupedSets = useMemo(() => {
    if (!dayExercise) return [];

    if (dayExercise.sets.length === 0) return [];

    const groups: {
      count: number;
      repetitions?: number;
      weight?: number | null;
      duration?: number;
      isReps: boolean;
    }[] = [];

    let currentGroup: (typeof groups)[0] | null = null;

    dayExercise.sets.forEach((set) => {
      const isReps = dayExercise.isRepsSet;
      const setData = {
        repetitions: isReps ? (set as RepsSet).repetitions : undefined,
        duration: !isReps ? (set as DurationSet).duration : undefined,
        weight: isReps ? (set as RepsSet)?.weight : undefined,
        isReps,
      };

      if (
        currentGroup &&
        currentGroup.repetitions === setData.repetitions &&
        currentGroup.duration === setData.duration &&
        currentGroup.weight === setData.weight &&
        currentGroup.isReps === setData.isReps
      ) {
        currentGroup.count++;
      } else {
        currentGroup = { ...setData, count: 1 };
        groups.push(currentGroup);
      }
    });

    return groups;
  }, [dayExercise]);

  const maxVisibleGroupSets = 2;
  const { visibleGroupedSets, hiddenGroupedSets, totalHiddenSets } = useMemo(() => {
    const visible = groupedSets.slice(0, maxVisibleGroupSets);
    const hiddenCount = groupedSets.length - maxVisibleGroupSets;
    const totalHidden = groupedSets
      .slice(maxVisibleGroupSets)
      .reduce((acc, group) => acc + group.count, 0);
    return {
      visibleGroupedSets: visible,
      hiddenGroupedSets: hiddenCount,
      totalHiddenSets: totalHidden,
    };
  }, [groupedSets, maxVisibleGroupSets]);

  return (
    <div
      className="rounded-md p-3 shadow-md flex items-center gap-2 border bg-card text-card-foreground relative flex-wrap min-[425px]:flex-nowrap"
      key={exercise._id}
    >
      <div className="flex-shrink-0 overflow-hidden h-full aspect-square max-h-18">
        <img
          alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
          className="w-full h-full object-cover rounded-md bg-primary/5 dark:grayscale-[0.2] dark:contrast-110"
          src={exercise.image}
        />
      </div>

      <div className="flex flex-col items-start justify-between min-[425px]:h-full">
        <Typography className="text-sm font-semibold line-clamp-1">{exercise.name}</Typography>
        <Typography className="text-sm line-clamp-1" variant="muted">
          {displayMuscleGroups}
        </Typography>
        <div className="flex items-center gap-1 flex-wrap">
          {visibleGroupedSets.map((group, index) => (
            <Badge className="text-xs" key={index} variant="outline">
              {group.count > 1 && `${group.count} x `}
              {group.isReps
                ? parseRepsLabel(group.repetitions!)
                : parseSecondsToTimeString(group.duration!)}
              {group.isReps && group.weight && ` • ${group.weight}kg`}
            </Badge>
          ))}
          {hiddenGroupedSets > 0 && (
            <Badge className="text-xs" variant="outline">
              +{parseWorkoutSetsLabel(totalHiddenSets)}
            </Badge>
          )}
          <Badge className="justify-start" variant="secondary">
            przerwa {parseSecondsToTimeString(restAfterSets)}
          </Badge>
        </div>
      </div>

      <div className="ml-auto absolute top-4 right-4 min-[425px]:static">
        <DetailsActionBtn redirectTo={`/exercises/${exercise._id}`} />
      </div>
    </div>
  );
}

interface WorkoutMetaInfoProps {
  workout: GetWorkoutApiResponse;
  isAuthor: boolean;
}

function WorkoutMetaInfo({ workout, isAuthor }: WorkoutMetaInfoProps) {
  const avgWorkoutTime = useMemo(() => {
    if (workout.days.length === 0) return 0;
    return Math.round(workout.totalDuration / workout.days.length);
  }, [workout]);

  return (
    <div className="flex flex-col gap-4 w-full col-span-full">
      <div className="flex flex-col gap-x-4 gap-y-4 sm:flex-row sm:flex-wrap sm:gap-x-8 md:gap-x-12">
        {workout.goal !== Goal.none && (
          <InfoField label="Cel treningowy" variant="p">
            <Badge className="min-h-12 bg-accent-foreground/10 text-foreground font-semibold px-3 text-lg">
              {workout.goal}
            </Badge>
          </InfoField>
        )}

        <InfoField label="Plan zawiera" variant="p">
          <div className="flex flex-wrap gap-2 items-center">
            <IconInfoField icon={CalendarDays} label={parseDayLabel(workout.days.length)} />
            <IconInfoField icon={BicepsFlexed} label={parseExerciseLabel(workout.totalExercises)} />
          </div>
        </InfoField>

        <InfoField className="sm:justify-self-end" label="Czas na codzienny trening" variant="p">
          <IconInfoField icon={Clock} label={parseTimeBrackets(avgWorkoutTime)} />
        </InfoField>
      </div>

      {workout.description && (
        <InfoField label="Opis planu" variant="p">
          <Typography
            className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent/60 text-balance"
            variant="p"
          >
            {workout.description}
          </Typography>
        </InfoField>
      )}

      <div className="flex flex-col md:flex-row gap-4 md:justify-between md:items-center flex-1">
        {workout.hashtags && workout.hashtags.length > 0 && (
          <HashtagList
            badgeClassName="min-h-7"
            className="max-h-24"
            fullWidth={false}
            hashtags={workout.hashtags}
            variant="p"
          />
        )}

        <div className="flex flex-col gap-1 md:justify-self-end md:text-right md:ml-auto">
          <Typography className="font-semibold">Dodane przez:</Typography>
          <DetailsAuthorBtn
            authorAvatar={workout.author.profileDetails?.profilePicture}
            authorName={workout.author.username}
            isAuthor={isAuthor}
            redirectTo={`/profile/${workout.author.username}`}
          />
        </div>
      </div>

      <Typography className="text-xs" variant="muted">
        {`Data dodania: ${new Date(workout.createdAt).toLocaleDateString()}`}
      </Typography>
    </div>
  );
}

export default WorkoutDetails;
