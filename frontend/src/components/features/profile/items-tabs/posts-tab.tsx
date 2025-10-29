import useGetUserPosts from '@/hooks/query/posts/use-get-user-posts';
import PostCreator from '../../posts/post-creator';
import PostList from '../../posts/post-list';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import ScrollLoader from '@/components/ui/scroll-loader';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import useOpen from '@/hooks/utils/use-open';
import { usePost } from '@/hooks/query/posts/use-post';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import PostDetails from '../../posts/post-details';
import { EditPostDialog } from '../../posts/post-dialogs';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import ReportDialog from '../../reports/report-dialog';
import { useUser } from '@/context/user';

interface PostsTabProps {
  isOwnProfile: boolean;
  profileId: string;
  isViewingAsGuest: boolean;
}

function PostsTab({ isOwnProfile, profileId, isViewingAsGuest }: PostsTabProps) {
  const deleteDialog = useOpen();
  const editDialog = useOpen();
  const detailsDialog = useOpen();
  const reportDialog = useOpen();

  const { user } = useUser();

  const { toggleLikePost, deletePost } = usePost();
  const { data, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetUserPosts(profileId);
  const {
    data: likedPosts,
    isLoading: isLikedPostsLoading,
    isError: isLikesPostsError,
  } = useGetLikedItems('posts');
  const { ref, inView } = useInView({
    threshold: 1,
  });

  const [selectedPostId, setSelectedPostId] = useState<string | null>(null);
  const [pendingLikePostId, setPendingLikePostId] = useState<string | null>(null);

  const posts = useMemo(() => data?.pages.flatMap((page) => page.items) ?? [], [data]);

  const selectedPost = useMemo(
    () => posts.find((post) => post._id === selectedPostId),
    [posts, selectedPostId],
  );

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage && !isLoading) {
      void fetchNextPage();
    }
  }, [inView, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading]);

  const handleToggleLike = useCallback(
    (postId: string) => {
      setPendingLikePostId(postId);
      toggleLikePost.mutate(postId, {
        onSettled: () => {
          setPendingLikePostId(null);
        },
      });
    },
    [toggleLikePost],
  );

  const handlePostEdit = useCallback(
    (postId: string) => {
      setSelectedPostId(postId);
      editDialog.open();
    },
    [editDialog],
  );

  const handleShowPostDetails = useCallback(
    (postId: string) => {
      setSelectedPostId(postId);
      detailsDialog.open();
    },
    [detailsDialog],
  );

  const handlePostDelete = useCallback(
    (postId: string) => {
      setSelectedPostId(postId);
      deleteDialog.open();
    },
    [deleteDialog],
  );

  const handleConfirmDelete = () => {
    if (selectedPostId) {
      deletePost.mutate(selectedPostId);
    }
  };

  const handlePostReport = useCallback(
    (postId: string) => {
      setSelectedPostId(postId);
      reportDialog.open();
    },
    [reportDialog],
  );

  return (
    <>
      <div className="flex flex-col gap-4 w-full h-full">
        {isOwnProfile && !isViewingAsGuest && <PostCreator mode="create" />}
        <PostList
          isError={isError}
          isLoading={isLoading}
          isViewingAsGuest={isViewingAsGuest}
          likePendingId={pendingLikePostId}
          likedPosts={{
            data: !isViewingAsGuest ? likedPosts : new Set<string>(),
            isLoading: isLikedPostsLoading,
            isError: isLikesPostsError,
          }}
          onDelete={handlePostDelete}
          onEdit={handlePostEdit}
          onLikeToggle={handleToggleLike}
          onReport={handlePostReport}
          onShowDetails={handleShowPostDetails}
          posts={posts}
        />
        <ScrollLoader
          hasNextPage={hasNextPage}
          isFetchingNextPage={isFetchingNextPage}
          itemLength={posts.length}
          itemType="post"
          ref={ref}
          showEndMessage
        />
        <ScrollToTopButton />
      </div>

      {selectedPost && (
        <PostDetails
          isViewingAsGuest={isViewingAsGuest}
          onClose={() => {
            setSelectedPostId(null);
            detailsDialog.close();
          }}
          onOpenChange={detailsDialog.setIsOpen}
          open={detailsDialog.isOpen}
          post={selectedPost}
        />
      )}

      {user && selectedPostId && (
        <ReportDialog
          isReply
          itemId={selectedPostId}
          itemType="Post"
          onClose={() => {
            setSelectedPostId(null);
            reportDialog.close();
          }}
          onOpenChange={reportDialog.setIsOpen}
          open={reportDialog.isOpen}
        />
      )}

      {isOwnProfile && selectedPost && (
        <EditPostDialog
          onClose={() => {
            setSelectedPostId(null);
            editDialog.close();
          }}
          onOpenChange={editDialog.setIsOpen}
          open={editDialog.isOpen}
          post={selectedPost}
        />
      )}

      {isOwnProfile && selectedPostId && (
        <ReusableAlertDialog
          cancelLabel="Anuluj"
          confirmLabel="Usuń"
          description="Usunięcie posta jest nieodwracalne. Wszystkie komentarze i polubienia zostaną usunięte."
          isLoading={deletePost.isPending}
          onClose={() => {
            setSelectedPostId(null);
            deleteDialog.close();
          }}
          onConfirm={handleConfirmDelete}
          open={deleteDialog.isOpen}
          title="Czy na pewno chcesz usunąć ten post?"
          variant="destructive"
        />
      )}
    </>
  );
}

export default PostsTab;
