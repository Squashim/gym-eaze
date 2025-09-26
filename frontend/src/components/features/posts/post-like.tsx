import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import { Loader2, ThumbsUp } from 'lucide-react';

interface PostLikeProps {
  isLiked: boolean;
  isLikedPostsError: boolean;
  isLikedPostsLoading: boolean;
  isLogged: boolean;
  isPending: boolean;
  likesCount: number;
  onToggleLike: () => void;
}

const PostLike = ({
  isLiked,
  isLikedPostsError,
  isLikedPostsLoading,
  isLogged,
  isPending,
  likesCount,
  onToggleLike,
}: PostLikeProps) => {
  if (isLogged) {
    if (isLikedPostsLoading || isLikedPostsError) {
      return (
        <Skeleton className="h-9 w-full flex-1/2 flex items-center justify-center ">
          <Loader2 className="animate-spin size-4" />
        </Skeleton>
      );
    }

    let buttonText = isLiked
      ? `Cofnij polubienie: ${likesCount}`
      : `Dodaj polubienie: ${likesCount}`;
    if (isPending) {
      buttonText = isLiked ? 'Cofanie...' : 'Dodawanie...';
    }

    return (
      <Button
        className="w-full flex-1/2"
        disabled={isPending}
        onClick={onToggleLike}
        variant="ghost"
      >
        {isPending ? (
          <Loader2 className="animate-spin size-5" />
        ) : (
          <ThumbsUp className={cn('size-5', isLiked && 'text-secondary ')} />
        )}
        {buttonText}
      </Button>
    );
  }

  return (
    <Button className="w-full flex-1/2 pointer-events-none" variant="ghost">
      <ThumbsUp className="size-5" />
      Lubi to: {likesCount}
    </Button>
  );
};

export default PostLike;
