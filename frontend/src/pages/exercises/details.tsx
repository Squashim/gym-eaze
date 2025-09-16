import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { AdvancedLevelBadge, LikesBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/advanced-level';
import { useUser } from '@/context/user';
import { useSequentialGuards } from '@/guards/use-sequential-guards';
import useGetExercise from '@/hooks/query/exercises/use-get-exercise';
import usePageTitle from '@/hooks/use-page-title';
import { cn, getApiErrorMsg } from '@/lib/utils';
import type { Equipment } from '@/types/equipment';
import type { MuscleGroup } from '@/types/muscle-group';
import { VideoOff } from 'lucide-react';
import { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import { useExercise } from '@/hooks/query/exercises/use-exercise';

import { toast } from 'sonner';
import ResourceActions from '@/components/ui/resource-actions';
import PageLayout from '@/components/layout/page';

const ExerciseDetails = () => {
  const { id } = useParams();
  const { user } = useUser();
  const { data: exercise, isLoading, isError, error } = useGetExercise(id!);
  const { toggleLikeExercise, toggleFavoriteExercise, deleteExercise } = useExercise();
  const isAuthor = useMemo(() => {
    if (!user || !exercise) return false;
    return user._id === exercise.author._id;
  }, [user, exercise]);

  const { data: userLikes, isLoading: isLikedItemsLoading } = useGetLikedItems('exercises');
  const { data: userFavorites, isLoading: isFavoritedItemsLoading } = useGetFavoritedItems(
    'exercises',
    {
      enabled: !isAuthor,
    },
  );

  const liked = useMemo(() => {
    if (!userLikes || !exercise) return false;
    if (userLikes.has(exercise?._id)) return true;
  }, [userLikes, exercise]);

  const favorited = useMemo(() => {
    if (!userFavorites || !exercise) return false;
    if (isAuthor) return false;
    if (userFavorites.has(exercise?._id)) return true;
  }, [userFavorites, exercise, isAuthor]);

  const handleLike = () => {
    toggleLikeExercise.mutate(exercise!._id);
  };

  const handleFavorite = () => {
    toggleFavoriteExercise.mutate(exercise!._id);
  };

  const handleDelete = () => {
    deleteExercise.mutate(exercise!._id);
  };

  const handleReport = () => {
    toast.info('Funkcja zgłaszania nie jest jeszcze dostępna.');
  };

  const guards = useMemo(
    () => [
      {
        check: () => {
          if (isLoading) return 'wait';
          if (isError || !exercise) return true;
          if (exercise.visibility === 'Publiczny') return true;
          if (exercise.visibility === 'Prywatny') {
            if (!user) return false;
            return isAuthor;
          }

          return false;
        },
        redirectTo: '/exercises/browse',
        errorMessage: 'Nie masz uprawnień do wyświetlenia tego ćwiczenia.',
      },
    ],
    [isLoading, isError, exercise, user, isAuthor],
  );

  useSequentialGuards(guards);
  usePageTitle(exercise?.name ?? 'Szczegóły ćwiczenia');

  const renderAuthorBtn = () => {
    if (!exercise) return null;

    return (
      <div className="flex items-center gap-2 md:justify-end">
        <Button
          asChild
          className="flex items-center gap-2 p-0 hover:bg-transparent"
          variant="ghost"
        >
          <a className="flex items-center gap-2 pr-2" href={`/profile/${exercise.author._id}`}>
            <Avatar className="shadow-sm">
              <AvatarImage
                alt={`zdjęcie profilowe użytkownika ${exercise.author.username}`}
                src={exercise.author.profileDetails.profilePicture || undefined}
              />
              <AvatarFallback className="dark:bg-background">
                <Typography variant="small">
                  {exercise.author.username.slice(0, 2).toUpperCase()}
                </Typography>
              </AvatarFallback>
            </Avatar>
            {exercise.author.username}
            {isAuthor && ' (Ty)'}
          </a>
        </Button>
      </div>
    );
  };

  return (
    <PageLayout>
      {isLoading ? (
        <ExerciseSkeleton />
      ) : !exercise ? (
        <EmptyExerciseCard error={error} isError={isError} />
      ) : (
        <>
          <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
            <header className="col-span-full flex flex-col md:flex-row md:items-center md:justify-between md:gap-4">
              <Typography className="text-left" variant="h1">
                {exercise.name}
              </Typography>
              {user && (
                <ResourceActions
                  className="mt-4 justify-end"
                  delete={{
                    onDelete: handleDelete,
                    isDeleteLoading: deleteExercise.isPending,
                    deleteTitle: `Czy na pewno chcesz usunąć ćwiczenie: ${exercise.name}`,
                    deleteDescription:
                      'Ta akcja jest nieodwracalna. Wszystkie dane związane z tym ćwiczeniem zostaną trwale usunięte.',
                  }}
                  edit={{ redirectTo: `/exercises/${exercise._id}/edit` }}
                  favorite={{
                    isFavorited: favorited || false,
                    isFavoriteLoading: toggleFavoriteExercise.isPending || isFavoritedItemsLoading,
                    onFavorite: handleFavorite,
                  }}
                  isAuthor={isAuthor}
                  like={{
                    isLiked: liked || false,
                    isLikeLoading: toggleLikeExercise.isPending || isLikedItemsLoading,
                    onLike: handleLike,
                  }}
                  report={{ onReport: handleReport }}
                />
              )}
            </header>
            <div className="w-full bg-accent/60 rounded-xl shadow-lg max-w-xl mx-auto">
              <video
                className="w-full aspect-square rounded-xl h-full object-contain dark:grayscale-[0.2] dark:contrast-110"
                controls
                loop
                muted
                src={exercise.video}
              >
                Twoja przeglądarka nie obsługuje odtwarzania filmów.
              </video>
            </div>
            <div className="flex flex-col gap-4 min-w-0">
              <div className="flex justify-between items-center gap-3">
                <div className="inline-flex gap-2">
                  {isAuthor && (
                    <VisibilityBadge
                      type="Exercise"
                      variant="accent"
                      visibility={exercise.visibility}
                    />
                  )}
                  {exercise.advancedLevel !== AdvancedLevel.none && (
                    <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
                  )}
                </div>

                <LikesBadge likes={exercise.likes} />
              </div>

              <div className="flex flex-col gap-1">
                <Typography className="font-semibold">Opis ćwiczenia:</Typography>
                <Typography
                  className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent/60"
                  variant="p"
                >
                  {exercise.description}
                </Typography>
              </div>

              <ItemsHorizontalGallery
                items={exercise.muscleGroup}
                label="Aktywne grupy mięśniowe"
              />
              <ItemsHorizontalGallery
                className="rounded-full"
                items={exercise.equipment}
                label="Potrzebny sprzęt ćwiczeniowy"
              />
            </div>
          </section>
          <div className="flex flex-col md:flex-row gap-4 md:justify-between md:items-center flex-1">
            {exercise.hashtags && exercise.hashtags.length > 0 && (
              <div className="flex flex-col gap-1">
                <Typography className="font-semibold">Hasztagi:</Typography>
                <div className="flex flex-wrap gap-1 max-h-24 overflow-y-auto scrollbar">
                  {exercise.hashtags.map((hashtag) => {
                    return (
                      <Badge className="min-h-7" key={hashtag} variant="outline">
                        {`#${hashtag}`}
                      </Badge>
                    );
                  })}
                </div>
              </div>
            )}

            <div className="flex flex-col gap-1 md:justify-self-end md:text-right md:ml-auto">
              <Typography className="font-semibold">Dodane przez:</Typography>
              {renderAuthorBtn()}
            </div>
          </div>

          <Typography className="text-xs" variant="muted">
            {`Data dodania: ${new Date(exercise.createdAt).toLocaleDateString()}`}
          </Typography>
        </>
      )}
    </PageLayout>
  );
};

export default ExerciseDetails;

const EmptyExerciseCard = ({ error, isError }: { error: Error | null; isError: boolean }) => {
  return (
    isError && (
      <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4">
        <Skeleton className="col-span-full min-h-18 animate-none bg-card border text-card-foreground flex items-center p-6">
          <Typography variant="h1">Coś poszło nie tak...</Typography>
        </Skeleton>
        <div className="w-full rounded-xl max-w-xl mx-auto">
          <Skeleton className="w-full aspect-square animate-none rounded-xl h-full bg-card border text-card-foreground flex items-center justify-center">
            <div className="size-124 rounded-full flex items-center justify-center">
              <VideoOff className="size-12 text-muted-foreground animate-pulse" />
            </div>
          </Skeleton>
        </div>
        <div className="flex flex-col gap-4">
          <Skeleton className="w-full min-h-60 animate-none bg-card border text-card-foreground p-6 flex flex-col gap-4">
            <Typography variant="h2">
              {getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd'}
            </Typography>
            <Typography className="max-w-xl" variant="lead">
              Nie udało się wczytać ćwiczenia. Spróbuj odświeżyć stronę lub wróć do listy ćwiczeń.
            </Typography>
            <Button asChild className="w-full sm:w-fit" size="lg">
              <a href="/exercises/browse">Powrót do listy ćwiczeń</a>
            </Button>
          </Skeleton>
          <Skeleton className="w-full h-40" />
          <Skeleton className="w-full h-40" />
        </div>
      </section>
    )
  );
};

const ExerciseSkeleton = () => {
  return (
    <>
      <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4">
        <Skeleton className="col-span-full h-18" />
        <div className="w-full rounded-xl max-w-xl mx-auto">
          <Skeleton className="w-full aspect-square rounded-xl h-full" />
        </div>
        <div className="flex flex-col gap-4">
          <Skeleton className="w-full h-60" />
          <Skeleton className="w-full h-40" />
          <Skeleton className="w-full h-40" />
        </div>
      </section>
      <Skeleton className="w-full h-17" />
    </>
  );
};

type ItemsHorizontalGalleryProps = {
  items: Equipment[] | MuscleGroup[];
  label: string;
  isLoading?: boolean;
  className?: string;
};

const ItemsHorizontalGallery = ({ items, label, className }: ItemsHorizontalGalleryProps) => {
  const renderItem = (item: Equipment | MuscleGroup) => {
    return (
      <div className="flex flex-col gap-3 items-center rounded-lg w-36 flex-none" key={item._id}>
        <img
          alt={item.name}
          className={cn(
            'size-27 md:size-24 aspect-square rounded-md object-cover bg-primary/10',
            className,
          )}
          loading="lazy"
          src={item.image}
        />

        <Typography className="text-center text-base leading-tight" variant="small">
          {item.name}
        </Typography>
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-1">
      <Typography className="font-semibold">{`${label}:`}</Typography>
      <div className="flex overflow-x-auto scrollbar gap-2 py-2">{items.map(renderItem)}</div>
    </div>
  );
};
