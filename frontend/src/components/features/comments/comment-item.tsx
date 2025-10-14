import { Button } from '@/components/ui/button';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Skeleton } from '@/components/ui/skeleton';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { parseTimeAgo } from '@/lib/parsers';
import { cn } from '@/lib/utils';
import type { GetCommentsApiResponse } from '@/types/comment';
import { ChevronDown, Loader2, ThumbsUp } from 'lucide-react';
import CommentManageDropdown from './comment-manage-dropdown';
import CommentList from './comment-list';
import useGetReplies from '@/hooks/query/comments/use-get-replies';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useComment } from '@/hooks/query/comments/use-comment';
import { useInView } from 'react-intersection-observer';
import useOpen from '@/hooks/utils/use-open';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { EditCommentDialog } from '../posts/post-dialogs';
import ScrollLoader from '@/components/ui/scroll-loader';
import ReportDialog from '../reports/report-dialog';

interface CommentItemProps {
  auth: {
    isLogged: boolean;
    isAuthor: boolean;
  };
  comment: GetCommentsApiResponse;
  like: {
    isLiked: boolean;
    isLikedCommentsLoading: boolean;
    isLikedCommentsError: boolean;
    isLikePending: boolean;
    likedComments?: Set<string>;
  };
  openDropdownId: string | null;
  replyRef?: React.RefObject<HTMLDivElement | null>;
  isReplying: boolean;
  setOpenDropdownId: (id: string | null) => void;
  onLikeToggle: (commentId: string) => void;
  onEdit: (commentId: string) => void;
  onDelete: (commentId: string) => void;
  onReport: (commentId: string) => void;
  onReply?: (commentId: string) => void;
}

function CommentItem({
  comment,
  onLikeToggle,
  onDelete,
  onEdit,
  onReport,
  onReply,
  setOpenDropdownId,
  isReplying,
  replyRef,
  openDropdownId,
  auth,
  like,
}: CommentItemProps) {
  const [showReplies, setShowReplies] = useState(false);
  const [pendingLikeReplyId, setPendingLikeReplyId] = useState<string | null>(null);
  const [selectedReplyId, setSelectedReplyId] = useState<string | null>(null);
  const editDialog = useOpen();
  const deleteDialog = useOpen();
  const reportDialog = useOpen();
  const { toggleLikeComment, deleteComment } = useComment({
    parentType: 'Komentarz',
    parentId: comment._id,
  });
  const { data, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetReplies(comment._id, {
      enabled: showReplies,
    });
  const { ref, inView } = useInView({
    threshold: 1,
  });

  const comments = useMemo(() => data?.pages.flatMap((page) => page.items) ?? [], [data]);

  const selectedReply = useMemo(
    () => comments.find((comment) => comment._id === selectedReplyId),
    [comments, selectedReplyId],
  );

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage && !isLoading) {
      void fetchNextPage();
    }
  }, [inView, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading]);

  const handleToggleReplies = () => {
    setShowReplies((prev) => !prev);
  };

  const handleToggleLikeReply = useCallback(
    (replyId: string) => {
      setPendingLikeReplyId(replyId);
      toggleLikeComment.mutate(replyId, {
        onSettled: () => {
          setPendingLikeReplyId(null);
        },
      });
    },
    [toggleLikeComment],
  );

  const handleReplyDelete = useCallback(
    (replyId: string) => {
      setSelectedReplyId(replyId);
      deleteDialog.open();
    },
    [deleteDialog],
  );

  const handleConfirmDelete = () => {
    if (selectedReplyId) {
      deleteComment.mutate(selectedReplyId, {
        onSuccess: () => {
          setSelectedReplyId(null);
          deleteDialog.close();
        },
      });
    }
  };

  const handleReplyEdit = useCallback(
    (replyId: string) => {
      setSelectedReplyId(replyId);
      editDialog.open();
    },
    [editDialog],
  );

  const handleReplyReport = useCallback(
    (replyId: string) => {
      setSelectedReplyId(replyId);
      reportDialog.open();
    },
    [reportDialog],
  );

  return (
    <>
      <div
        className="flex gap-2 items-start w-full"
        key={comment._id}
        ref={isReplying ? replyRef : null}
      >
        <Button asChild className="size-8 rounded-3xl p-0" variant="ghost">
          <a
            className="hover:ring-ring/50 hover:ring-[3px] rounded-3xl"
            href={`/profile/${comment.author._id}`}
            rel="noreferrer"
            target="_blank"
          >
            <ReusableAvatar
              className="cursor-pointer shadow-sm size-8"
              profilePicture={comment.author.profileDetails.profilePicture}
              username={comment.author.username}
            />
          </a>
        </Button>

        <div className="flex flex-col gap-1 w-full min-w-0">
          <div className="flex gap-1 items-center">
            <div className="flex flex-col gap-2 rounded-xl bg-card border px-2 pt-3 pb-2 min-w-0 max-h-80 overflow-y-auto scrollbar">
              <Button
                asChild
                className="p-0 justify-start w-fit h-fit focus-within:underline"
                variant="link"
              >
                <a href={`/profile/${comment.author._id}`} rel="noreferrer" target="_blank">
                  <Typography className="font-semibold text-sm leading-2">
                    {comment.author.username} {auth.isAuthor && '(Ty)'}
                  </Typography>
                </a>
              </Button>

              <Typography className="break-words text-sm">{comment.content}</Typography>
            </div>

            {auth.isLogged && (
              <CommentManageDropdown
                isAuthor={auth.isAuthor}
                isOpen={openDropdownId === comment._id}
                onDelete={() => onDelete(comment._id)}
                onEdit={() => onEdit(comment._id)}
                onOpenChange={(isOpen) => setOpenDropdownId(isOpen ? comment._id : null)}
                onReport={() => onReport(comment._id)}
                type={onReply ? 'comment' : 'reply'}
              />
            )}
          </div>

          <div className="flex flex-wrap gap-1 text-xs items-center px-1">
            <Typography className="text-xs pr-2 text-muted-foreground" variant="small">
              {parseTimeAgo(comment.createdAt)}
              {comment.isEdited && ' (edytowano)'}
            </Typography>

            {auth.isLogged ? (
              like.isLikedCommentsError || like.isLikedCommentsLoading ? (
                <Skeleton className="h-6 w-10 flex items-center justify-center">
                  <Loader2 className="animate-spin size-4" />
                </Skeleton>
              ) : (
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      className={cn(
                        'text-xs gap-1 p-0 h-auto py-1 !px-2 min-w-10',
                        like.isLiked && 'text-secondary',
                      )}
                      disabled={like.isLikePending}
                      onClick={() => onLikeToggle(comment._id)}
                      type="button"
                      variant="ghost"
                    >
                      {like.isLikePending ? (
                        <Loader2 className="animate-spin size-4" />
                      ) : (
                        <>
                          <ThumbsUp />
                          {comment.likes}
                        </>
                      )}
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>
                    {like.isLiked ? 'Cofnij polubienie' : 'Dodaj polubienie'}
                  </TooltipContent>
                </Tooltip>
              )
            ) : (
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    className="text-xs gap-1 p-0 h-auto py-1 !px-2"
                    type="button"
                    variant="ghost"
                  >
                    <ThumbsUp />
                    {comment.likes}
                  </Button>
                </TooltipTrigger>
                <TooltipContent>Lubi to</TooltipContent>
              </Tooltip>
            )}

            {onReply && (
              <Button
                className="text-xs p-0 h-auto py-1 px-2"
                disabled={!auth.isLogged || isReplying}
                onClick={() => onReply(comment._id)}
                type="button"
                variant={isReplying ? 'secondary' : 'ghost'}
              >
                {isReplying ? 'Odpowiadasz' : 'Odpowiedz'}
              </Button>
            )}

            {(comment.repliesCount ?? 0) > 0 && (
              <Button
                className="text-xs p-0 h-auto py-1 px-2 gap-1"
                onClick={handleToggleReplies}
                type="button"
                variant="ghost"
              >
                {showReplies ? 'Ukryj odpowiedzi' : `Zobacz odpowiedzi (${comment.repliesCount})`}
                <ChevronDown
                  className={cn(
                    'rotate-0 transition-[rotate] duration-300 ease-in-out',
                    showReplies && 'rotate-180',
                  )}
                />
              </Button>
            )}

            {showReplies && (comment.repliesCount ?? 0) > 0 && (
              <div className="flex flex-col gap-3 mt-2 w-full max-h-[40vh] overflow-y-auto scrollbar ">
                <CommentList
                  comments={comments}
                  isError={isError}
                  isLoading={isLoading}
                  likePendingId={pendingLikeReplyId}
                  likedComments={{
                    data: like.likedComments,
                    isLoading: like.isLikedCommentsLoading,
                    isError: like.isLikedCommentsError,
                  }}
                  onDelete={handleReplyDelete}
                  onEdit={handleReplyEdit}
                  onLikeToggle={handleToggleLikeReply}
                  onReport={handleReplyReport}
                  openDropdownId={openDropdownId}
                  setOpenDropdownId={setOpenDropdownId}
                />
                <ScrollLoader
                  hasNextPage={hasNextPage}
                  isFetchingNextPage={isFetchingNextPage}
                  itemLength={comments.length}
                  itemType="reply"
                  ref={ref}
                />
              </div>
            )}
          </div>
        </div>
      </div>

      {auth.isLogged && selectedReplyId && (
        <ReusableAlertDialog
          cancelLabel="Anuluj"
          confirmLabel="Usuń"
          description="Usunięcie odpowiedzi jest nieodwracalne. Wszystkie polubienia zostaną usunięte."
          isLoading={deleteComment.isPending}
          onClose={() => {
            setSelectedReplyId(null);
            deleteDialog.close();
          }}
          onConfirm={handleConfirmDelete}
          open={deleteDialog.isOpen}
          title="Czy na pewno chcesz usunąć tą odpowiedź?"
          variant="destructive"
        />
      )}

      {auth.isLogged && selectedReply && (
        <ReportDialog
          isReply
          itemId={selectedReply._id}
          itemType="Komentarz"
          onClose={() => {
            setSelectedReplyId(null);
            reportDialog.close();
          }}
          onOpenChange={reportDialog.setIsOpen}
          open={reportDialog.isOpen}
        />
      )}

      {auth.isLogged && selectedReply && (
        <EditCommentDialog
          comment={selectedReply}
          onClose={() => {
            setSelectedReplyId(null);
            editDialog.close();
          }}
          onOpenChange={editDialog.setIsOpen}
          open={editDialog.isOpen}
          parentId={comment._id}
          parentType="Komentarz"
        />
      )}
    </>
  );
}

export default CommentItem;
