import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { usePost } from '@/hooks/query/posts/use-post';
import { parseTimeAgo } from '@/lib/parsers';
import { cn } from '@/lib/utils';
import type { GetPostsApiResponse } from '@/types/post';
import {
  EllipsisVertical,
  Flag,
  Loader2,
  MessageSquare,
  Pencil,
  ThumbsUp,
  Trash2,
  X,
} from 'lucide-react';
import { useCallback } from 'react';

interface PostItemProps {
  post: GetPostsApiResponse;
  isLogged?: boolean;
  isAuthor?: boolean;
  isLiked?: boolean;
  isLikedPostsLoading: boolean;
  isLikedPostsError: boolean;
}

function PostItem({
  post,
  isAuthor,
  isLiked,
  isLikedPostsError,
  isLikedPostsLoading,
  isLogged,
}: PostItemProps) {
  const { toggleLikePost } = usePost();

  const handleToggleLike = useCallback(() => {
    toggleLikePost.mutate(post._id);
  }, [post._id, toggleLikePost]);

  return (
    <div className="bg-card text-card-foreground rounded-xl border p-4 shadow-sm flex flex-col gap-2 max-w-xl">
      <header className="flex gap-4 justify-between items-center w-full">
        <div className="flex gap-2 items-center">
          <Button asChild className="size-10 rounded-3xl p-0" variant="ghost">
            <a
              className="hover:ring-ring/50 hover:ring-[3px] rounded-3xl"
              href={`/profile/${post.author._id}`}
              rel="noreferrer"
              target="_blank"
            >
              <ReusableAvatar
                className="cursor-pointer shadow-sm size-10"
                fallbackClassName="dark:bg-background"
                profilePicture={post.author.profileDetails.profilePicture}
                username={post.author.username}
              />
            </a>
          </Button>
          <div className="flex flex-col">
            <Button asChild className="p-0 justify-start w-fit h-fit" variant="link">
              <a href={`/profile/${post.author._id}`} rel="noreferrer" target="_blank">
                <Typography className="font-semibold">
                  {post.author.username} {isAuthor && '(Ty)'}
                </Typography>
              </a>
            </Button>

            <Typography className="text-sm" variant="muted">
              {parseTimeAgo(post.createdAt)}
            </Typography>
          </div>
        </div>
        {isLogged && <PostManageDropdown isAuthor={!!isAuthor} />}
      </header>
      <Typography className="break-words whitespace-break-spaces max-h-50 overflow-y-auto scrollbar">
        {post.content}
      </Typography>

      {post.image && <MediaPreview author={post.author.username} src={post.image} type="image" />}

      {post.video && <MediaPreview author={post.author.username} src={post.video} type="video" />}

      {post.hashtags && post.hashtags.length > 0 && (
        <div className="flex flex-nowrap gap-2 overflow-y-auto scrollbar px-1 py-2">
          {post.hashtags.map((hashtag) => (
            <Badge className="px-3 py-1.5 rounded-md" key={hashtag} variant="outline">
              {`#${hashtag}`}
            </Badge>
          ))}
        </div>
      )}

      <div className="flex flex-col justify-center min-[500px]:flex-row gap-2 mt-2 max-[500px]:items-center">
        <LikeButton
          isLiked={isLiked}
          isLikedPostsError={isLikedPostsError}
          isLikedPostsLoading={isLikedPostsLoading}
          isLogged={isLogged}
          isSubmitting={toggleLikePost.isPending}
          likesCount={post.likes}
          onToggleLike={handleToggleLike}
        />

        <Separator
          className="bg-primary/20 rounded-md hidden min-[500px]:block"
          orientation="vertical"
        />

        <Button className="w-full flex-1/2" variant="ghost">
          <MessageSquare className="size-5" />
          Zobacz komentarze: 0
        </Button>
      </div>
    </div>
  );
}

interface PostManageDropdownProps {
  isAuthor: boolean;
}

const PostManageDropdown = ({ isAuthor }: PostManageDropdownProps) => {
  return (
    <DropdownMenu>
      <Tooltip>
        <TooltipTrigger asChild>
          <DropdownMenuTrigger asChild tabIndex={0}>
            <Button size="icon" variant="ghost">
              <EllipsisVertical />
            </Button>
          </DropdownMenuTrigger>
        </TooltipTrigger>
        <TooltipContent>
          <Typography variant="small">Więcej opcji posta</Typography>
        </TooltipContent>
      </Tooltip>
      <DropdownMenuContent align="end">
        {isAuthor ? (
          <>
            <DropdownMenuItem>
              <Pencil />
              Edytuj post
            </DropdownMenuItem>
            <DropdownMenuItem variant="destructive">
              <Trash2 />
              Usuń post
            </DropdownMenuItem>
          </>
        ) : (
          <DropdownMenuItem>
            <Flag />
            Zgłoś post
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

interface LikeButtonProps {
  isLogged?: boolean;
  isSubmitting: boolean;
  isLiked: boolean | undefined;
  isLikedPostsLoading: boolean;
  isLikedPostsError: boolean;
  likesCount: number;
  onToggleLike: () => void;
}

const LikeButton = ({
  isLogged,
  isLiked,
  likesCount,
  isSubmitting,
  onToggleLike,
  isLikedPostsError,
  isLikedPostsLoading,
}: LikeButtonProps) => {
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
    if (isSubmitting) {
      buttonText = isLiked ? 'Cofanie...' : 'Dodawanie...';
    }

    return (
      <Button
        className="w-full flex-1/2"
        disabled={isSubmitting}
        onClick={onToggleLike}
        variant="ghost"
      >
        {isSubmitting ? (
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

interface MediaPreviewProps {
  type: 'image' | 'video';
  src: string;
  author: string;
}

const MediaPreview = ({ type, src, author }: MediaPreviewProps) => {
  return (
    <Dialog>
      <DialogTrigger asChild>
        <div
          className="h-96 aspect-video rounded-xl overflow-hidden relative bg-primary/5 cursor-pointer"
          title="Kliknij, aby powiększyć"
        >
          {type === 'image' ? (
            <img
              alt={`zdjęcie do postu użytkownika ${author}`}
              className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
              loading="lazy"
              src={src}
            />
          ) : (
            <video
              className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
              controls
              src={src}
            >
              Twoja przeglądarka nie obsługuje odtwarzania filmów.
            </video>
          )}
        </div>
      </DialogTrigger>
      <DialogContent className="max-w-7xl border-0 bg-transparent shadow-none [&>button]:hidden">
        <DialogHeader>
          <DialogTitle className="sr-only">
            {type === 'image' ? 'Zdjęcie' : 'Filmik'} do postu użytkownika {author}
          </DialogTitle>
          <DialogClose asChild className="ml-auto">
            <Button size="icon">
              <X />
            </Button>
          </DialogClose>
        </DialogHeader>
        <div className="relative h-[calc(100vh-220px)] w-full overflow-clip rounded-md bg-transparent shadow-md">
          {type === 'image' ? (
            <img
              alt={`zdjęcie do postu użytkownika ${author}`}
              className="h-full w-full object-contain"
              loading="lazy"
              src={src}
            />
          ) : (
            <video className="w-full h-full object-contain" controls src={src}>
              Twoja przeglądarka nie obsługuje odtwarzania filmów.
            </video>
          )}
        </div>
        <DialogDescription className="sr-only">
          {`Pełny widok ${type === 'image' ? 'zdjęcia' : 'filmiku'} do postu użytkownika ${author}`}
        </DialogDescription>
      </DialogContent>
    </Dialog>
  );
};

export default PostItem;
