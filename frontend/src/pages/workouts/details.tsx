import PageLayout from '@/components/layout/page';
import { Badge } from '@/components/ui/badge';
import { AdvancedLevelBadge, LikesBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import ResourceActions from '@/components/ui/resource-actions';
import { DetailsAuthorBtn } from '@/components/ui/reusable-buttons';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/advanced-level';
import { useUser } from '@/context/user';
import { useSequentialGuards } from '@/guards/use-sequential-guards';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import useGetWorkout from '@/hooks/query/workouts/use-get-workout';
import { useWorkout } from '@/hooks/query/workouts/use-workout';
import usePageTitle from '@/hooks/utils/use-page-title';
import { useCallback, useMemo } from 'react';
import { useParams } from 'react-router-dom';

function WorkoutDetails() {
  const { user } = useUser();
  const { id } = useParams();
  const { data: workout, isLoading, isError, error } = useGetWorkout(id!);
  const { deleteWorkout, toggleFavoriteWorkout, toggleLikeWorkout } = useWorkout();

  const isAuthor = useMemo(() => {
    if (!user || !workout) return false;
    return user._id === workout.author._id;
  }, [user, workout]);

  const { data: userLikes, isLoading: isLikedItemsLoading } = useGetLikedItems('trainingPlans');
  const { data: userFavorites, isLoading: isFavoritedItemsLoading } = useGetFavoritedItems(
    'trainingPlans',
    {
      enabled: !isAuthor,
    },
  );

  const liked = useMemo(() => {
    if (!userLikes || !workout) return false;
    if (userLikes.has(workout?._id)) return true;
  }, [userLikes, workout]);

  const favorited = useMemo(() => {
    if (!userFavorites || !workout) return false;
    if (isAuthor) return false;
    if (userFavorites.has(workout?._id)) return true;
  }, [userFavorites, workout, isAuthor]);

  const guards = useMemo(
    () => [
      {
        check: () => {
          if (isLoading) return 'wait';
          if (isError || !workout) return true;
          if (workout.visibility === 'Publiczny') return true;
          if (workout.visibility === 'Prywatny') {
            if (!user) return false;
            return isAuthor;
          }

          return false;
        },
        redirectTo: '/workouts/browse',
        errorMessage: 'Nie masz uprawnień do wyświetlenia tego planu.',
      },
    ],
    [isLoading, isError, workout, user, isAuthor],
  );

  useSequentialGuards(guards);
  usePageTitle(workout?.name ?? 'Szczegóły planu');

  const handleLike = useCallback(() => {
    toggleLikeWorkout.mutate(workout!._id);
  }, [toggleLikeWorkout, workout]);

  const handleFavorite = useCallback(() => {
    toggleFavoriteWorkout.mutate(workout!._id);
  }, [toggleFavoriteWorkout, workout]);

  const handleDelete = useCallback(() => {
    deleteWorkout.mutate(workout!._id);
  }, [deleteWorkout, workout]);

  const handleReport = useCallback(() => {
    //TODO: implement report functionality
  }, []);

  const handleDownload = useCallback(() => {
    //TODO: implement download functionality
  }, []);

  return (
    <PageLayout>
      {isLoading ? (
        <p>Ładowanie</p>
      ) : !workout ? (
        <p>Błąd serwera</p>
      ) : (
        <>
          <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
            <header className="col-span-full flex flex-col md:flex-row md:items-center md:justify-between md:gap-4">
              <Typography className="text-left" variant="h1">
                {workout.name}
              </Typography>
              {user && (
                <ResourceActions
                  className="mt-4 justify-end"
                  delete={{
                    onDelete: handleDelete,
                    isDeleteLoading: deleteWorkout.isPending,
                    deleteTitle: `Czy na pewno chcesz usunąć plan treningowy: ${workout.name}`,
                    deleteDescription:
                      'Ta akcja jest nieodwracalna. Wszystkie dane związane z tym planem zostaną trwale usunięte.',
                  }}
                  download={{ isDownloading: false, onDownload: handleDownload }}
                  edit={{ redirectTo: `/workouts/${workout._id}/edit` }}
                  favorite={{
                    isFavorited: favorited || false,
                    isFavoriteLoading: toggleFavoriteWorkout.isPending || isFavoritedItemsLoading,
                    onFavorite: handleFavorite,
                  }}
                  isAuthor={isAuthor}
                  like={{
                    isLiked: liked || false,
                    isLikeLoading: toggleLikeWorkout.isPending || isLikedItemsLoading,
                    onLike: handleLike,
                  }}
                  report={{ onReport: handleReport }}
                />
              )}
            </header>
            <div className="w-full bg-accent/60 rounded-xl shadow-lg max-w-xl mx-auto">
              <img
                alt={`Zdjęcie planu treningowego ${workout.name}`}
                className="w-full aspect-square rounded-xl h-full object-contain dark:grayscale-[0.2] dark:contrast-110"
                loading="lazy"
                src={workout.image}
              />
            </div>
            <div className="flex flex-col gap-4 min-w-0">
              <div className="flex justify-between items-center gap-3 flex-wrap">
                <div className="inline-flex gap-2">
                  {isAuthor && (
                    <VisibilityBadge
                      type="Workout"
                      variant="accent"
                      visibility={workout.visibility}
                    />
                  )}
                  {workout.advancedLevel !== AdvancedLevel.none && (
                    <AdvancedLevelBadge advancedLevel={workout.advancedLevel} />
                  )}
                </div>

                <LikesBadge likes={workout.likes} />
              </div>

              <div className="flex items-center gap-1">
                <Typography className="font-semibold">Cel treningowy:</Typography>
                <Badge className="min-h-7">{workout.goal}</Badge>
              </div>
            </div>
          </section>

          {workout.description && (
            <div className="flex flex-col gap-1">
              <Typography className="font-semibold">Opis planu:</Typography>
              <Typography
                className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent/60 text-balance"
                variant="p"
              >
                {workout.description}
              </Typography>
            </div>
          )}

          <div className="flex flex-col md:flex-row gap-4 md:justify-between md:items-center flex-1">
            {workout.hashtags && workout.hashtags.length > 0 && (
              <div className="flex flex-col gap-1">
                <Typography className="font-semibold">Hasztagi:</Typography>
                <div className="flex flex-wrap gap-1 max-h-24 overflow-y-auto scrollbar">
                  {workout.hashtags.map((hashtag) => {
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
              <DetailsAuthorBtn
                authorAvatar={workout.author.profileDetails?.profilePicture}
                authorName={workout.author.username}
                isAuthor={isAuthor}
                redirectTo={`/profile/${workout.author._id}`}
              />
            </div>
          </div>

          <Typography className="text-xs" variant="muted">
            {`Data dodania: ${new Date(workout.createdAt).toLocaleDateString()}`}
          </Typography>
        </>
      )}
    </PageLayout>
  );
}

export default WorkoutDetails;
