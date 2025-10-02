import PageLayout from '@/components/layout/page';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { AdvancedLevelBadge, LikesBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import ResourceActions from '@/components/ui/resource-actions';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/advanced-level';
import { useUser } from '@/context/user';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import { useCallback, useMemo } from 'react';

function WorkoutDetails() {
  const { user } = useUser();

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

  const handleLike = useCallback(() => {
    //TODO: implement like functionality
  }, []);

  const handleFavorite = useCallback(() => {
    //TODO: implement favorite functionality
  }, []);

  const handleDelete = useCallback(() => {
    //TODO: implement delete functionality
  }, []);

  const handleReport = useCallback(() => {
    //TODO: implement report functionality
  }, []);

  const handleDownload = useCallback(() => {
    //TODO: implement download functionality
  }, []);

  const renderAuthorBtn = () => {
    if (!workout) return null;

    return (
      <div className="flex items-center gap-2 md:justify-end">
        <Button
          asChild
          className="flex items-center gap-2 p-0 hover:bg-transparent rounded-tl-2xl rounded-bl-2xl"
          variant="ghost"
        >
          <a className="flex items-center gap-2 pr-2" href={`/profile/${workout.author._id}`}>
            <ReusableAvatar
              className="shadow-sm"
              fallbackClassName="dark:bg-background"
              profilePicture={user?.profileDetails.profilePicture}
              username={user?.username}
            />
            {workout.author.username}
            {isAuthor && ' (Ty)'}
          </a>
        </Button>
      </div>
    );
  };

  return (
    <PageLayout>
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
                  isDeleteLoading: false, // TODO: deleteExercise.isPending,
                  deleteTitle: `Czy na pewno chcesz usunąć plan treningowy: ${workout.name}`,
                  deleteDescription:
                    'Ta akcja jest nieodwracalna. Wszystkie dane związane z tym planem zostaną trwale usunięte.',
                }}
                download={{ isDownloading: false, onDownload: handleDownload }}
                edit={{ redirectTo: `/workouts/${workout._id}/edit` }}
                favorite={{
                  isFavorited: favorited || false,
                  isFavoriteLoading: false, //TODO: toggleFavoriteExercise.isPending || isFavoritedItemsLoading,
                  onFavorite: handleFavorite,
                }}
                isAuthor={isAuthor}
                like={{
                  isLiked: liked || false,
                  isLikeLoading: false, //TODO: toggleLikeExercise.isPending || isLikedItemsLoading,
                  onLike: handleLike,
                }}
                report={{ onReport: handleReport }}
              />
            )}
          </header>
          {/* <div className="w-full bg-accent/60 rounded-xl shadow-lg max-w-xl mx-auto">
            <video
              className="w-full aspect-square rounded-xl h-full object-contain dark:grayscale-[0.2] dark:contrast-110"
              controls
              loop
              muted
              src={exercise.video}
            >
              Twoja przeglądarka nie obsługuje odtwarzania filmów.
            </video>
          </div> */}
          <div className="flex flex-col gap-4 min-w-0">
            <div className="flex justify-between items-center gap-3">
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

            <div className="flex flex-col gap-1">
              <Typography className="font-semibold">Opis planu:</Typography>
              <Typography
                className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent/60"
                variant="p"
              >
                {workout.description}
              </Typography>
            </div>
          </div>
        </section>
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
            {renderAuthorBtn()}
          </div>
        </div>

        <Typography className="text-xs" variant="muted">
          {`Data dodania: ${new Date(workout.createdAt).toLocaleDateString()}`}
        </Typography>
      </>
    </PageLayout>
  );
}

export default WorkoutDetails;
