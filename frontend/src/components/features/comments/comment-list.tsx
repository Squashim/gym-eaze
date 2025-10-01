import type { GetCommentsApiResponse } from '@/types/comment';
import CommentItem from './comment-item';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Info } from 'lucide-react';
import { useMemo } from 'react';
import { useUser } from '@/context/user';

interface CommentListProps {
  comments?: GetCommentsApiResponse[];
  likedComments: {
    data?: Set<string>;
    isLoading: boolean;
    isError: boolean;
  };
  openDropdownId: string | null;
  setOpenDropdownId: (id: string | null) => void;
  isLoading: boolean;
  isError: boolean;
  likePendingId: string | null;
  replyCommentId?: string | null;
  replyRef?: React.RefObject<HTMLDivElement | null>;
  onLikeToggle: (commentId: string) => void;
  onEdit: (commentId: string) => void;
  onDelete: (commentId: string) => void;
  onReport: (commentId: string) => void;
  onReply?: (commentId: string) => void;
}

function CommentList({
  comments,
  isLoading,
  isError,
  onLikeToggle,
  onEdit,
  onDelete,
  onReport,
  onReply,
  likedComments,
  openDropdownId,
  setOpenDropdownId,
  likePendingId,
  replyCommentId,
  replyRef,
}: CommentListProps) {
  const { user } = useUser();

  const isLiked = useMemo(() => {
    return (commentId: string) => likedComments.data?.has(commentId) ?? false;
  }, [likedComments]);

  const isAuthor = useMemo(() => {
    return (authorId: string) => !!user && user._id === authorId;
  }, [user]);

  const isLikePending = useMemo(() => {
    return (commentId: string) => likePendingId === commentId;
  }, [likePendingId]);

  const isReplyComment = useMemo(() => {
    return (commentId: string) => replyCommentId === commentId;
  }, [replyCommentId]);

  if (isLoading) {
    return Array.from({ length: 4 }).map((_, index) => <Skeleton className="h-16" key={index} />);
  }

  if (isError) {
    return (
      <Alert>
        <Info />
        <AlertTitle>Wystąpił błąd podczas pobierania komentarzy. Spróbuj ponownie</AlertTitle>
      </Alert>
    );
  }

  if (!comments || (comments.length === 0 && onReply)) {
    return (
      <Alert>
        <Info />
        <AlertTitle> Bądź pierwszą osobą, która doda komentarz!</AlertTitle>
      </Alert>
    );
  }

  return comments.map((comment) => (
    <CommentItem
      auth={{
        isLogged: !!user,
        isAuthor: isAuthor(comment.author._id),
      }}
      comment={comment}
      isReplying={isReplyComment(comment._id)}
      key={comment._id}
      like={{
        likedComments: likedComments.data,
        isLikePending: isLikePending(comment._id),
        isLiked: isLiked(comment._id),
        isLikedCommentsLoading: likedComments.isLoading,
        isLikedCommentsError: likedComments.isError,
      }}
      onDelete={onDelete}
      onEdit={onEdit}
      onLikeToggle={onLikeToggle}
      onReply={onReply}
      onReport={onReport}
      openDropdownId={openDropdownId}
      replyRef={replyRef}
      setOpenDropdownId={setOpenDropdownId}
    />
  ));
}

export default CommentList;
