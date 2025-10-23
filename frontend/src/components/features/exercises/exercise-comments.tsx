import Typography from '@/components/ui/typography';
import { useUser } from '@/context/user';
import useGetExerciseComments from '@/hooks/query/exercises/use-get-exercise-comments';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import useOpen from '@/hooks/utils/use-open';
import type { GetExerciseApiResponse } from '@/types/exercise';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import CommentCreator from '../comments/comment-creator';
import CommentList from '../comments/comment-list';
import ScrollLoader from '@/components/ui/scroll-loader';
import { useComment } from '@/hooks/query/comments/use-comment';
import { toast } from 'sonner';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { EditCommentDialog } from '../posts/post-dialogs';
import { Button } from '@/components/ui/button';
import { Loader2, RefreshCw } from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import ReportDialog from '../reports/report-dialog';

interface ExerciseCommentsProps {
  exercise: GetExerciseApiResponse;
}

function ExerciseComments({ exercise }: ExerciseCommentsProps) {
  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    refetch,
    isRefetching,
  } = useGetExerciseComments(exercise._id);
  const {
    data: likedComments,
    isLoading: isLikedCommentsLoading,
    isError: isLikedCommentsError,
    refetch: refetchLikedComments,
  } = useGetLikedItems('comments');
  const { user } = useUser();

  const { toggleLikeComment, deleteComment } = useComment({
    parentType: 'Ćwiczenie',
    parentId: exercise._id,
  });

  const deleteDialog = useOpen();
  const editDialog = useOpen();

  const { ref, inView } = useInView({
    threshold: 1,
  });
  const exerciseRef = useRef<HTMLDivElement>(null);
  const textAreaRef = useRef<HTMLTextAreaElement>(null);
  const replyRef = useRef<HTMLDivElement>(null);
  const reportDialog = useOpen();

  const [selectedCommentId, setSelectedCommentId] = useState<string | null>(null);
  const [replyCommentId, setReplyCommentId] = useState<string | null>(null);
  const [pendingLikeCommentId, setPendingLikeCommentId] = useState<string | null>(null);
  const [openDropdownId, setOpenDropdownId] = useState<string | null>(null);

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
    (commentId: string) => {
      setSelectedCommentId(commentId);
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
    if (textAreaRef.current) {
      textAreaRef.current.focus();
    }
  }, []);

  const handleCommentReplyClose = useCallback(() => {
    setReplyCommentId(null);
    if (replyRef.current) {
      replyRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
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
    await refetchLikedComments();
    toast.success('Komentarze zostały odświeżone');
  }, [refetch, refetchLikedComments]);

  const handleCommentReport = useCallback(
    (commentId: string) => {
      setSelectedCommentId(commentId);
      reportDialog.open();
    },
    [reportDialog],
  );

  return (
    <>
      <div className="col-span-full flex flex-col gap-4 max-w-4xl mt-4 w-full" ref={exerciseRef}>
        <div className="flex items-center justify-between flex-wrap gap-2">
          <Typography className="border-0" variant="h2">
            Komentarze ({exercise.commentsCount ?? 0})
          </Typography>
          <Button
            disabled={isRefetching}
            onClick={handleCommentsRefresh}
            type="button"
            variant="outline"
          >
            {isRefetching ? (
              <>
                <Loader2 className="animate-spin" />
                Odświeżanie...
              </>
            ) : (
              <>
                <RefreshCw />
                Odśwież komentarze
              </>
            )}
          </Button>
        </div>

        <Separator />

        {replyCommentId && user && (
          <CommentCreator
            className="p-0"
            onClose={handleCommentReplyClose}
            parentId={replyCommentId}
            parentType="Komentarz"
            ref={textAreaRef}
            replyingTo={replyingTo}
            scrollRef={replyRef}
          />
        )}

        {!replyCommentId && (
          <CommentCreator
            autoFocus={false}
            className="p-0"
            parentId={exercise._id}
            parentType="Ćwiczenie"
          />
        )}

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

        <ScrollToTopButton />
      </div>

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

      {!!user && selectedCommentId && (
        <ReportDialog
          itemId={selectedCommentId}
          itemType="Komentarz"
          onClose={() => {
            setSelectedCommentId(null);
            reportDialog.close();
          }}
          onOpenChange={reportDialog.setIsOpen}
          open={reportDialog.isOpen}
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
          parentId={exercise._id}
          parentType="Ćwiczenie"
        />
      )}
    </>
  );
}

export default ExerciseComments;
