import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { AdvancedLevelBadge, ItemDetailsBadge, LikesBadge } from '@/components/ui/item-info-badges';
import { Separator } from '@/components/ui/separator';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Goal } from '@/constants/enums/goals';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import {
  parseExerciseLabel,
  parseTimeBrackets,
  parseWorkoutDaysLabel,
  parseWorkoutDurationLabel,
} from '@/lib/parsers';
import type { PageType } from '@/types/filters';
import type { GetWorkoutsApiResponse } from '@/types/workout';
import { Plus, Search } from 'lucide-react';
import { useMemo } from 'react';
import { Link } from 'react-router-dom';

interface WorkoutListProps {
  isFetchingError?: boolean;
  pageType?: PageType;
  workouts?: GetWorkoutsApiResponse[];
}

function WorkoutList({ workouts, isFetchingError, pageType }: WorkoutListProps) {
  const { data: favoritedWorkouts, isLoading: isFavoritedWorkoutsLoading } = useGetFavoritedItems(
    'trainingPlans',
    {
      enabled: pageType !== 'user',
    },
  );
  const { data: likedWorkouts, isLoading: isLikedWorkoutsLoading } =
    useGetLikedItems('trainingPlans');

  const isDetailsLoading = useMemo(() => {
    return isFavoritedWorkoutsLoading || isLikedWorkoutsLoading;
  }, [isFavoritedWorkoutsLoading, isLikedWorkoutsLoading]);

  if (!workouts || workouts.length === 0) {
    return <EmptyWorkoutCard isFetchingError={isFetchingError} pageType={pageType} />;
  }

  return workouts.map((workout) => (
    <WorkoutCard
      key={workout._id}
      {...workout}
      isDetailsLoading={isDetailsLoading}
      isFavorited={favoritedWorkouts?.has(workout._id)}
      isLiked={likedWorkouts?.has(workout._id)}
      pageType={pageType}
    />
  ));
}

type EmptyWorkoutCardProps = Omit<WorkoutListProps, 'workouts'>;

const EmptyWorkoutCard = ({ isFetchingError, pageType = 'browser' }: EmptyWorkoutCardProps) => {
  const getMessage = () => {
    switch (pageType) {
      case 'user':
        return {
          title: 'Nie masz jeszcze żadnego planu!',
          description: 'Stwórz swój pierwszy plan i zacznij dzielić się nim z innymi!',
        };
      case 'favorite':
        return {
          title: 'Brak ulubionych planów!',
          description: 'Dodaj plan do ulubionych, aby mieć go zawsze pod ręką.',
        };

      default:
        return {
          title: 'Nic nie znaleziono!',
          description: 'Spróbuj zmienić lub usunąć niektóre filtry.',
        };
    }
  };

  return (
    <Card className="col-span-full min-h-36 justify-center">
      <CardHeader>
        <div className="flex flex-col items-center justify-center text-center space-y-2">
          <CardTitle className="text-2xl">
            {isFetchingError ? 'Błąd podczas pobierania planów' : getMessage().title}
          </CardTitle>
          <CardDescription className="text-base text-muted-foreground max-w-sm">
            {isFetchingError
              ? 'Wystąpił błąd podczas pobierania planów. Spróbuj odświeżyć stronę lub wrócić później.'
              : getMessage().description}
          </CardDescription>
        </div>
      </CardHeader>
      {pageType !== 'browser' && (
        <CardContent className="flex justify-center">
          <Button asChild size="lg">
            <Link to={pageType === 'user' ? '/workouts/create' : '/workouts/browse'}>
              {pageType === 'user' ? <Plus /> : <Search />}
              {pageType === 'user' ? 'Dodaj plan treningowy' : 'Przeglądaj plany treningowe'}
            </Link>
          </Button>
        </CardContent>
      )}
    </Card>
  );
};

type WorkoutCardProps = GetWorkoutsApiResponse & {
  isLiked?: boolean;
  isFavorited?: boolean;
  isDetailsLoading?: boolean;
  pageType?: PageType;
};

const WorkoutCard = ({
  isLiked,
  isFavorited,
  isDetailsLoading,
  pageType = 'browser',
  ...workout
}: WorkoutCardProps) => {
  const avgTrainingTime = useMemo(() => {
    return workout.totalDuration / workout.daysCount.training;
  }, [workout.totalDuration, workout.daysCount.training]);

  const maxVisibleHashtags = 3;
  const { visibleHashtags, hiddenHashtagsCount } = useMemo(() => {
    if (!workout.hashtags) {
      return {
        visibleHashtags: [],
        hiddenHashtagsCount: 0,
      };
    }
    const visible = workout.hashtags.slice(0, maxVisibleHashtags);
    const hiddenCount = workout.hashtags.length - maxVisibleHashtags;
    return {
      visibleHashtags: visible,
      hiddenHashtagsCount: hiddenCount > 0 ? hiddenCount : 0,
    };
  }, [workout.hashtags]);

  return (
    <a
      className="rounded-xl outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] max-w-md mx-auto w-full"
      href={`/workouts/${workout._id}`}
      rel="noreferrer"
      target="_blank"
    >
      <Card className="group hover:shadow-xl hover:shadow-primary/40 hover:border-primary hover:bg-gradient-to-br hover:from-card hover:to-primary/5 transition-all duration-300 ease-in-out h-full transform hover:-translate-y-1">
        <CardHeader className="gap-2 ">
          <div className="flex justify-between items-center gap-3">
            <div>
              {workout.advancedLevel !== AdvancedLevel.none && (
                <AdvancedLevelBadge advancedLevel={workout.advancedLevel} />
              )}
            </div>

            <LikesBadge likes={workout.likes} />
          </div>

          <div className="flex w-full aspect-[4/3] rounded-xl overflow-hidden relative">
            <img
              alt={`zdjęcie poglądowe ćwiczenia ${workout.name}`}
              className="w-full h-full object-cover rounded-xl transition-transform duration-500 ease-out group-hover:scale-105 dark:grayscale-[0.2] dark:contrast-110 bg-primary/5"
              src={workout.image}
            />
            <ItemDetailsBadge
              isFavorited={isFavorited}
              isLiked={isLiked}
              isLoading={isDetailsLoading}
              isPublic={workout.visibility === 'Publiczny'}
              isUserPage={pageType === 'user'}
            />
          </div>

          <CardTitle className="text-xl line-clamp-2">{workout.name}</CardTitle>
          <CardDescription>
            {`${parseWorkoutDurationLabel(workout.daysCount.total)} • ${parseExerciseLabel(workout.totalExercises)} • ${parseTimeBrackets(avgTrainingTime)}`}
          </CardDescription>
          <CardContent className="p-0 flex flex-col gap-3">
            <div className="flex flex-wrap gap-2 justify-between items-center">
              {workout.goal !== Goal.none && <Badge variant="accent">{workout.goal}</Badge>}

              <div className="flex gap-2 items-center flex-wrap ml-auto">
                <Badge variant="accent">
                  {parseWorkoutDaysLabel(workout.daysCount.training, 'training')}
                </Badge>
                {workout.daysCount.rest > 0 && (
                  <Badge className="text-secondary" variant="accent">
                    {parseWorkoutDaysLabel(workout.daysCount.rest, 'rest')}
                  </Badge>
                )}
              </div>
            </div>

            {workout.hashtags && workout.hashtags.length > 0 && (
              <>
                <Separator />
                <div className="flex flex-wrap gap-1">
                  {visibleHashtags.map((hashtag) => (
                    <Badge key={hashtag} variant="outline">
                      {`#${hashtag}`}
                    </Badge>
                  ))}
                  {hiddenHashtagsCount > 0 && (
                    <Badge variant="outline">+{hiddenHashtagsCount} więcej</Badge>
                  )}
                </div>
              </>
            )}
          </CardContent>
        </CardHeader>
      </Card>
    </a>
  );
};

export default WorkoutList;
