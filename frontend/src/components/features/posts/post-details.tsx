import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { parseTimeAgo } from '@/lib/parsers';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import useGetPostsComments from '@/hooks/query/comments/use-get-post-comments';
import CommentList from '../comments/comment-list';
import CommentCreator from '../comments/comment-creator';
import useOpen from '@/hooks/utils/use-open';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { useInView } from 'react-intersection-observer';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import type { GetPostsApiResponse } from '@/types/post';
import { useUser } from '@/context/user';
import { useComment } from '@/hooks/query/comments/use-comment';
import { Loader2, MessageSquare, RefreshCw, ThumbsUp, X } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import Typography from '@/components/ui/typography';
import { EditCommentDialog } from './post-dialogs';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { toast } from 'sonner';
import ScrollLoader from '@/components/ui/scroll-loader';

interface PostDetailsProps {
  open: boolean;
  post: GetPostsApiResponse;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

const PostDetails = ({ open, post, onOpenChange, onClose }: PostDetailsProps) => {
  const { user } = useUser();
  const deleteDialog = useOpen();
  const editDialog = useOpen();
  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    refetch,
    isRefetching,
  } = useGetPostsComments(post._id);
  const { toggleLikeComment, deleteComment } = useComment({
    parentType: 'Post',
    parentId: post._id,
  });
  const {
    data: likedComments,
    isLoading: isLikedCommentsLoading,
    isError: isLikedCommentsError,
  } = useGetLikedItems('comments');
  const { ref, inView } = useInView({
    threshold: 1,
  });
  const postRef = useRef<HTMLDivElement>(null);
  const replyRef = useRef<HTMLDivElement>(null);

  const [selectedCommentId, setSelectedCommentId] = useState<string | null>(null);
  const [replyCommentId, setReplyCommentId] = useState<string | null>(null);
  const [pendingLikeCommentId, setPendingLikeCommentId] = useState<string | null>(null);
  const [openDropdownId, setOpenDropdownId] = useState<string | null>(null);

  const isAuthor = useMemo(() => {
    return !!user && user._id === post.author._id;
  }, [user, post.author._id]);

  const comments = useMemo(() => data?.pages.flatMap((page) => page.items) ?? [], [data]);

  const selectedComment = useMemo(
    () => comments.find((comment) => comment._id === selectedCommentId),
    [comments, selectedCommentId],
  );

  const replyingTo = useMemo(() => {
    if (replyCommentId) {
      return comments?.find((comment) => comment._id === replyCommentId)?.author.username || null;
    }
  }, [comments, replyCommentId]);

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage && !isLoading) {
      void fetchNextPage();
    }
  }, [inView, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading]);

  const handleToggleLikeComment = useCallback(
    (commentId: string) => {
      setPendingLikeCommentId(commentId);
      toggleLikeComment.mutate(commentId, {
        onSettled: () => {
          setPendingLikeCommentId(null);
        },
      });
    },
    [toggleLikeComment],
  );

  const handleCommentDelete = useCallback(
    (postId: string) => {
      setSelectedCommentId(postId);
      deleteDialog.open();
    },
    [deleteDialog],
  );

  const handleConfirmDelete = () => {
    if (selectedCommentId) {
      deleteComment.mutate(selectedCommentId, {
        onSuccess: () => {
          setSelectedCommentId(null);
          deleteDialog.close();
        },
      });
    }
  };

  const handleCommentReply = useCallback((commentId: string) => {
    setReplyCommentId(commentId);
  }, []);

  const handleCommentReplyClose = useCallback(() => {
    setReplyCommentId(null);
  }, []);

  const handleCommentEdit = useCallback(
    (commentId: string) => {
      setSelectedCommentId(commentId);
      editDialog.open();
    },
    [editDialog],
  );

  const handleCommentsRefresh = useCallback(async () => {
    await refetch();
    if (postRef.current) {
      postRef.current.scrollIntoView({ behavior: 'smooth' });
    }
    toast.success('Komentarze zostały odświeżone');
  }, [refetch]);

  const handleCommentReport = useCallback((commentId: string) => {
    console.log('TODO: Report comment', commentId);
  }, []);

  return (
    <>
      <Dialog onOpenChange={onOpenChange} open={open}>
        <DialogContent
          className="[&>button]:hidden p-0 gap-0 sm:max-w-2xl flex flex-col max-h-[95vh] h-[95vh]"
          onEscapeKeyDown={(e) => {
            if (openDropdownId) {
              e.preventDefault();
              e.stopPropagation();
              setOpenDropdownId(null);
            }

            if (replyCommentId) {
              handleCommentReplyClose();
            }
          }}
          onInteractOutside={(e) => e.preventDefault()}
        >
          <PostHeader
            isRefetching={isRefetching}
            onClose={onClose}
            onRefresh={handleCommentsRefresh}
            post={post}
          />
          <Separator />
          <div className="flex-1 min-h-0 p-4 overflow-y-auto scrollbar">
            <PostContent isAuthor={isAuthor} post={post} postRef={postRef} />

            <div className="flex flex-col gap-3">
              <CommentList
                comments={comments}
                isError={isError}
                isLoading={isLoading}
                likePendingId={pendingLikeCommentId}
                likedComments={{
                  data: likedComments,
                  isLoading: isLikedCommentsLoading,
                  isError: isLikedCommentsError,
                }}
                onDelete={handleCommentDelete}
                onEdit={handleCommentEdit}
                onLikeToggle={handleToggleLikeComment}
                onReply={handleCommentReply}
                onReport={handleCommentReport}
                openDropdownId={openDropdownId}
                replyCommentId={replyCommentId}
                replyRef={replyRef}
                setOpenDropdownId={setOpenDropdownId}
              />
              <ScrollLoader
                hasNextPage={hasNextPage}
                isFetchingNextPage={isFetchingNextPage}
                itemLength={comments.length}
                itemType="comment"
                ref={ref}
              />
            </div>
          </div>

          <Separator />

          {replyCommentId && user && (
            <CommentCreator
              onClose={handleCommentReplyClose}
              parentId={replyCommentId}
              parentType="Komentarz"
              replyingTo={replyingTo}
              scrollRef={replyRef}
            />
          )}

          {!replyCommentId && (
            <CommentCreator parentId={post._id} parentType="Post" scrollRef={postRef} />
          )}
        </DialogContent>
      </Dialog>

      {!!user && selectedCommentId && (
        <ReusableAlertDialog
          cancelLabel="Anuluj"
          confirmLabel="Usuń"
          description="Usunięcie komentarza jest nieodwracalne. Wszystkie odpowiedzi i polubienia zostaną usunięte."
          isLoading={deleteComment.isPending}
          onClose={() => {
            setSelectedCommentId(null);
            deleteDialog.close();
          }}
          onConfirm={handleConfirmDelete}
          open={deleteDialog.isOpen}
          title="Czy na pewno chcesz usunąć ten komentarz?"
          variant="destructive"
        />
      )}

      {!!user && selectedComment && (
        <EditCommentDialog
          comment={selectedComment}
          onClose={() => {
            setSelectedCommentId(null);
            editDialog.close();
          }}
          onOpenChange={editDialog.setIsOpen}
          open={editDialog.isOpen}
          parentId={post._id}
          parentType="Post"
        />
      )}
    </>
  );
};

interface PostHeaderProps {
  post: GetPostsApiResponse;
  isRefetching: boolean;
  onRefresh: () => void;
  onClose: () => void;
}

function PostHeader({ post, onRefresh, isRefetching, onClose }: PostHeaderProps) {
  return (
    <DialogHeader className="grid grid-cols-[1fr_auto_1fr] items-center gap-4 p-4">
      <div className="col-start-2">
        <DialogTitle className="truncate">Post {post.author.username}</DialogTitle>
        <DialogDescription className="sr-only">
          Zobacz szczegóły posta i komentarze.
        </DialogDescription>
      </div>

      <div className="col-start-1 row-start-1 justify-self-start">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              disabled={isRefetching}
              onClick={onRefresh}
              size="icon"
              type="button"
              variant="outline"
            >
              {isRefetching ? <Loader2 className="animate-spin" /> : <RefreshCw />}
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <Typography variant="small">Odśwież komentarze</Typography>
          </TooltipContent>
        </Tooltip>
      </div>

      <Button
        className="justify-self-end col-start-3 row-start-1"
        onClick={onClose}
        size="icon"
        type="button"
        variant="outline"
      >
        <X />
      </Button>
    </DialogHeader>
  );
}

interface PostContentProps {
  post: GetPostsApiResponse;
  isAuthor: boolean;
  postRef: React.RefObject<HTMLDivElement | null>;
}

function PostContent({ post, isAuthor, postRef }: PostContentProps) {
  return (
    <>
      <header className="flex gap-4 justify-between items-center">
        <div className="flex gap-2 items-center" ref={postRef}>
          <Button asChild className="size-10 rounded-3xl p-0" variant="ghost">
            <a
              className="hover:ring-ring/50 hover:ring-[3px] rounded-3xl"
              href={`/profile/${post.author._id}`}
              rel="noreferrer"
              target="_blank"
            >
              <ReusableAvatar
                className="cursor-pointer shadow-sm size-10"
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
              {parseTimeAgo(post.createdAt)} {post.isEdited && '(edytowano)'}
            </Typography>
          </div>
        </div>
      </header>

      <Typography className="break-words whitespace-pre-line mt-2">{post.content}</Typography>

      <PostMedia post={post} />

      <PostHashtags post={post} />

      <PostStats post={post} />
    </>
  );
}

interface PostMediaProps {
  post: GetPostsApiResponse;
}

function PostMedia({ post }: PostMediaProps) {
  if (post.image) {
    return (
      <div className="h-96 aspect-video max-w-full rounded-xl overflow-hidden relative bg-primary/5 mt-2">
        <img
          alt={`zdjęcie do postu użytkownika ${post.author.username}`}
          className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
          loading="lazy"
          src={post.image}
        />
      </div>
    );
  }

  if (post.video) {
    return (
      <div className="h-96 aspect-video max-w-full rounded-xl overflow-hidden relative bg-primary/5 mt-2">
        <video
          className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
          controls
          src={post.video}
        >
          Twoja przeglądarka nie obsługuje odtwarzania filmów.
        </video>
      </div>
    );
  }

  return null;
}

interface PostHashtagsProps {
  post: GetPostsApiResponse;
}

function PostHashtags({ post }: PostHashtagsProps) {
  if (post.hashtags && post.hashtags.length > 0) {
    return (
      <div className="flex flex-nowrap gap-2 overflow-x-auto scrollbar px-1 py-2">
        {post.hashtags.map((hashtag) => (
          <Badge className="px-3 py-1.5 rounded-md" key={hashtag} variant="outline">
            {`#${hashtag}`}
          </Badge>
        ))}
      </div>
    );
  }

  return null;
}

interface PostStatsProps {
  post: GetPostsApiResponse;
}

function PostStats({ post }: PostStatsProps) {
  return (
    <div className="w-full flex items-center gap-4 my-4 flex-wrap">
      <Badge className="min-h-7 text-sm px-3" variant="accent">
        <div>
          <ThumbsUp className="size-4 mr-0.5" />
        </div>
        {post.likes > 0 ? `Polubienia (${post.likes})` : 'Brak polubień'}
      </Badge>
      <Badge className=" text-sm" variant="accent">
        <div>
          <MessageSquare className="size-4" />
        </div>
        {post.commentsCount && post.commentsCount > 0
          ? `Komentarze (${post.commentsCount})`
          : 'Brak komentarzy'}
      </Badge>
    </div>
  );
}

export default PostDetails;
