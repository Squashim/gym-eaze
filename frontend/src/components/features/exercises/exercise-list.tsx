import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { AdvancedLevelBadge, ItemDetailsBadge, LikesBadge } from '@/components/ui/item-info-badges';
import { Separator } from '@/components/ui/separator';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import type { GetExercisesApiResponse } from '@/types/exercise';
import type { PageType } from '@/types/filters';
import { Plus, Search } from 'lucide-react';
import { useMemo } from 'react';
import { Link } from 'react-router-dom';

type ExerciseListProps = {
  isFetchingError?: boolean;
  pageType?: PageType;
  exercises?: GetExercisesApiResponse[];
};

const ExerciseList = ({ exercises, isFetchingError, pageType = 'browser' }: ExerciseListProps) => {
  const { data: favoritedExercises, isLoading: isFavoritedExercisesLoading } = useGetFavoritedItems(
    'exercises',
    {
      enabled: pageType !== 'user',
    },
  );
  const { data: likedExercises, isLoading: isLikedExercisesLoading } =
    useGetLikedItems('exercises');

  const isDetailsLoading = useMemo(() => {
    return isFavoritedExercisesLoading || isLikedExercisesLoading;
  }, [isFavoritedExercisesLoading, isLikedExercisesLoading]);

  if (!exercises || exercises.length === 0) {
    return <EmptyExerciseCard isFetchingError={isFetchingError} pageType={pageType} />;
  }

  return exercises.map((ex) => (
    <ExerciseCard
      key={ex._id}
      {...ex}
      isDetailsLoading={isDetailsLoading}
      isFavorited={favoritedExercises?.has(ex._id)}
      isLiked={likedExercises?.has(ex._id)}
      pageType={pageType}
    />
  ));
};

type EmptyExerciseCardProps = Omit<ExerciseListProps, 'exercises'>;

const EmptyExerciseCard = ({ isFetchingError, pageType = 'browser' }: EmptyExerciseCardProps) => {
  const getMessage = () => {
    switch (pageType) {
      case 'user':
        return {
          title: 'Nie masz jeszcze żadnego ćwiczenia!',
          description: 'Stwórz swoje pierwsze ćwiczenie i zacznij dzielić się nim z innymi!',
        };
      case 'favorite':
        return {
          title: 'Brak ulubionych ćwiczeń!',
          description: 'Dodaj ćwiczenia do ulubionych, aby mieć je zawsze pod ręką.',
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
            {isFetchingError ? 'Błąd podczas pobierania ćwiczeń' : getMessage().title}
          </CardTitle>
          <CardDescription className="text-base text-muted-foreground max-w-sm">
            {isFetchingError
              ? 'Wystąpił błąd podczas pobierania ćwiczeń. Spróbuj odświeżyć stronę lub wrócić później.'
              : getMessage().description}
          </CardDescription>
        </div>
      </CardHeader>
      {pageType !== 'browser' && (
        <CardContent className="flex justify-center">
          <Button asChild size="lg">
            <Link to={pageType === 'user' ? '/exercises/create' : '/exercises/browse'}>
              {pageType === 'user' ? <Plus /> : <Search />}
              {pageType === 'user' ? 'Dodaj ćwiczenie' : 'Przeglądaj ćwiczenia'}
            </Link>
          </Button>
        </CardContent>
      )}
    </Card>
  );
};

type ExerciseCardProps = GetExercisesApiResponse & {
  isLiked?: boolean;
  isFavorited?: boolean;
  isDetailsLoading?: boolean;
  pageType?: PageType;
};

const ExerciseCard = ({
  isLiked,
  isFavorited,
  isDetailsLoading,
  pageType = 'browser',
  ...exercise
}: ExerciseCardProps) => {
  const displayMuscleGroups = exercise.muscleGroup.map((mg) => mg.name).join(' • ');

  const maxVisibleHashtags = 3;
  const { visibleHashtags, hiddenHashtagsCount } = useMemo(() => {
    if (!exercise.hashtags) {
      return {
        visibleHashtags: [],
        hiddenHashtagsCount: 0,
      };
    }
    const visible = exercise.hashtags.slice(0, maxVisibleHashtags);
    const hiddenCount = exercise.hashtags.length - maxVisibleHashtags;
    return {
      visibleHashtags: visible,
      hiddenHashtagsCount: hiddenCount > 0 ? hiddenCount : 0,
    };
  }, [exercise.hashtags]);

  return (
    <a
      className="rounded-xl outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] max-w-md mx-auto w-full"
      href={`/exercises/${exercise._id}`}
      rel="noreferrer"
      target="_blank"
    >
      <Card className="group hover:shadow-xl hover:shadow-primary/40 hover:border-primary hover:bg-gradient-to-br hover:from-card hover:to-primary/5 transition-all duration-300 ease-in-out h-full transform hover:-translate-y-1 gap-4">
        <CardHeader className="gap-2 ">
          <div className="flex justify-between items-center gap-3">
            <div>
              {exercise.advancedLevel !== AdvancedLevel.none && (
                <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
              )}
            </div>

            <LikesBadge likes={exercise.likes} />
          </div>

          <div className="flex w-full aspect-[4/3] rounded-xl overflow-hidden relative">
            <img
              alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
              className="w-full h-full object-cover rounded-xl transition-transform duration-500 ease-out group-hover:scale-105 dark:grayscale-[0.2] dark:contrast-110 bg-primary/5"
              src={exercise.image}
            />
            <ItemDetailsBadge
              isFavorited={isFavorited}
              isLiked={isLiked}
              isLoading={isDetailsLoading}
              isPublic={exercise.visibility === 'Publiczny'}
              isUserPage={pageType === 'user'}
            />
          </div>

          <CardTitle className="text-xl line-clamp-2">{exercise.name}</CardTitle>
          <CardDescription>{displayMuscleGroups}</CardDescription>
          <CardContent className="p-0 flex flex-col gap-3">
            {exercise.hashtags && exercise.hashtags.length > 0 && (
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
        {pageType !== 'user' && (
          <CardFooter className="mt-auto ml-auto text-sm text-muted-foreground">
            Dodane przez: {exercise.author.username}
          </CardFooter>
        )}
      </Card>
    </a>
  );
};

export default ExerciseList;
