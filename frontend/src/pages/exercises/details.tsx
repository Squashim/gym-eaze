import { Button } from '@/components/ui/button';
import { AdvancedLevelBadge, LikesBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/advanced-level';
import { useUser } from '@/context/user';
import { useSequentialGuards } from '@/guards/use-sequential-guards';
import useGetExercise from '@/hooks/query/exercises/use-get-exercise';
import usePageTitle from '@/hooks/utils/use-page-title';
import { cn, getApiErrorMsg } from '@/lib/utils';
import type { Equipment } from '@/types/equipment';
import type { MuscleGroup } from '@/types/muscle-group';
import { VideoOff } from 'lucide-react';
import { useCallback, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import useGetFavoritedItems from '@/hooks/query/user/use-get-favorited-items';
import { useExercise } from '@/hooks/query/exercises/use-exercise';
import ResourceActions from '@/components/ui/resource-actions';
import PageLayout from '@/components/layout/page';
import { DetailsAuthorBtn } from '@/components/ui/reusable-buttons';
import type { GetExerciseApiResponse } from '@/types/exercise';
import { InfoField } from '@/components/ui/info-fields';
import HashtagList from '@/components/ui/hashtag-list';

const ExerciseDetails = () => {
  const { id } = useParams();
  const { user } = useUser();
  const { data: exercise, isLoading, isError, error } = useGetExercise(id!);

  const isAuthor = useMemo(() => {
    if (!user || !exercise) return false;
    return user._id === exercise.author._id;
  }, [user, exercise]);

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

  return (
    <PageLayout>
      <ExerciseContent
        auth={{ isAuthor, isLogged: !!user }}
        error={error}
        exercise={exercise}
        isError={isError}
        isLoading={isLoading}
      />
    </PageLayout>
  );
};

export default ExerciseDetails;

interface ExerciseContentProps {
  auth: { isAuthor: boolean; isLogged: boolean };
  exercise?: GetExerciseApiResponse;
  isLoading: boolean;
  isError: boolean;
  error: Error | null;
}

function ExerciseContent({ auth, exercise, isLoading, isError, error }: ExerciseContentProps) {
  if (isLoading) {
    return <ExerciseSkeleton />;
  }

  if (isError || !exercise) {
    return <EmptyExerciseCard error={error} />;
  }

  return (
    <section className="grid grid-cols-1 md:grid-cols-[0.75fr_1fr] gap-4 w-full">
      <ExerciseHeader auth={auth} exercise={exercise} />

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
        <div className="flex justify-between items-center gap-3 flex-wrap">
          <div className="inline-flex gap-2">
            {auth.isAuthor && (
              <VisibilityBadge type="Exercise" variant="accent" visibility={exercise.visibility} />
            )}
            {exercise.advancedLevel !== AdvancedLevel.none && (
              <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
            )}
          </div>

          <LikesBadge likes={exercise.likes} />
        </div>

        <InfoField label="Opis ćwiczenia" variant="p">
          <Typography
            className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent/60"
            variant="p"
          >
            {exercise.description}
          </Typography>
        </InfoField>

        <ItemsHorizontalGallery items={exercise.muscleGroup} label="Aktywne grupy mięśniowe" />

        <ItemsHorizontalGallery
          className="rounded-full"
          items={exercise.equipment}
          label="Potrzebny sprzęt ćwiczeniowy"
        />
      </div>

      <ExerciseMetaInfo exercise={exercise} isAuthor={auth.isAuthor} />
    </section>
  );
}

const EmptyExerciseCard = ({ error }: { error: Error | null }) => {
  return (
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

interface ExerciseHeaderProps {
  exercise: GetExerciseApiResponse;
  auth: { isAuthor: boolean; isLogged: boolean };
}

function ExerciseHeader({ exercise, auth }: ExerciseHeaderProps) {
  const { toggleLikeExercise, toggleFavoriteExercise, deleteExercise } = useExercise();
  const { data: userLikes, isLoading: isLikedItemsLoading } = useGetLikedItems('exercises');
  const { data: userFavorites, isLoading: isFavoritedItemsLoading } = useGetFavoritedItems(
    'exercises',
    {
      enabled: !auth.isAuthor,
    },
  );

  const liked = useMemo(() => {
    if (!userLikes || !exercise) return false;
    if (userLikes.has(exercise?._id)) return true;
  }, [userLikes, exercise]);

  const favorited = useMemo(() => {
    if (!userFavorites || !exercise) return false;
    if (auth.isAuthor) return false;
    if (userFavorites.has(exercise?._id)) return true;
  }, [userFavorites, exercise, auth.isAuthor]);

  const handleLike = useCallback(() => {
    toggleLikeExercise.mutate(exercise._id);
  }, [toggleLikeExercise, exercise._id]);

  const handleFavorite = useCallback(() => {
    toggleFavoriteExercise.mutate(exercise._id);
  }, [toggleFavoriteExercise, exercise._id]);

  const handleDelete = useCallback(() => {
    deleteExercise.mutate(exercise._id);
  }, [deleteExercise, exercise._id]);

  const handleReport = useCallback(() => {
    //TODO: implement report functionality
  }, []);

  return (
    <header className="col-span-full flex flex-col md:flex-row md:items-center md:justify-between md:gap-4">
      <Typography className="text-left" variant="h1">
        {exercise.name}
      </Typography>
      {auth.isLogged && (
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
          isAuthor={auth.isAuthor}
          like={{
            isLiked: liked || false,
            isLikeLoading: toggleLikeExercise.isPending || isLikedItemsLoading,
            onLike: handleLike,
          }}
          report={{ onReport: handleReport }}
        />
      )}
    </header>
  );
}

interface ExerciseMetaInfoProps {
  exercise: GetExerciseApiResponse;
  isAuthor: boolean;
}

function ExerciseMetaInfo({ exercise, isAuthor }: ExerciseMetaInfoProps) {
  return (
    <div className="flex flex-col gap-4 w-full col-span-full">
      <div className="flex flex-col md:flex-row gap-4 md:justify-between md:items-center flex-1">
        {exercise.hashtags && exercise.hashtags.length > 0 && (
          <HashtagList
            badgeClassName="min-h-7"
            className="max-h-24"
            fullWidth={false}
            hashtags={exercise.hashtags}
            variant="p"
          />
        )}

        <div className="flex flex-col gap-1 md:justify-self-end md:text-right md:ml-auto">
          <Typography className="font-semibold">Dodane przez:</Typography>
          <DetailsAuthorBtn
            authorAvatar={exercise.author.profileDetails?.profilePicture}
            authorName={exercise.author.username}
            isAuthor={isAuthor}
            redirectTo={`/profile/${exercise.author._id}`}
          />
        </div>
      </div>

      <Typography className="text-xs" variant="muted">
        {`Data dodania: ${new Date(exercise.createdAt).toLocaleDateString()}`}
      </Typography>
    </div>
  );
}

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
