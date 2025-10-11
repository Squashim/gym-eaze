import PostCreator from '@/components/features/posts/post-creator';
import PostDetails from '@/components/features/posts/post-details';
import { EditPostDialog } from '@/components/features/posts/post-dialogs';
import PostFilters from '@/components/features/posts/post-filters';
import PostList from '@/components/features/posts/post-list';
import PageLayout from '@/components/layout/page';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import ScrollLoader from '@/components/ui/scroll-loader';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import { useUser } from '@/context/user';
import useGetPosts from '@/hooks/query/posts/use-get-posts';
import { usePost } from '@/hooks/query/posts/use-post';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import usePostParamsQuery from '@/hooks/filters-pagination/use-post-params-query';
import useOpen from '@/hooks/utils/use-open';
import usePageTitle from '@/hooks/utils/use-page-title';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useInView } from 'react-intersection-observer';

function Community() {
  const { params, setHashtags, setParam, clearParams } = usePostParamsQuery();

  const { user } = useUser();
  const deleteDialog = useOpen();
  const editDialog = useOpen();
  const detailsDialog = useOpen();
  const { toggleLikePost, deletePost } = usePost();
  const { data, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetPosts(params);
  const {
    data: likedPosts,
    isLoading: isLikedPostsLoading,
    isError: isLikesPostsError,
  } = useGetLikedItems('posts');
  const { ref, inView } = useInView({
    threshold: 1,
  });
  usePageTitle('Przeglądaj posty');
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

  const handlePostReport = useCallback((postId: string) => {
    console.log('TODO: Report post', postId);
  }, []);

  return (
    <>
      <PageLayout className="max-w-xl sm:max-w-2xl" title="Najnowsze posty">
        {user && <PostCreator mode="create" />}
        <PostFilters
          clearParams={clearParams}
          isLogged={!!user}
          params={params}
          setHashtags={setHashtags}
          setParam={setParam}
        />
        <PostList
          isError={isError}
          isLoading={isLoading}
          likePendingId={pendingLikePostId}
          likedPosts={{
            data: likedPosts,
            isLoading: isLikedPostsLoading,
            isError: isLikesPostsError,
          }}
          onDelete={handlePostDelete}
          onEdit={handlePostEdit}
          onLikeToggle={handleToggleLike}
          onReport={handlePostReport}
          onShowDetails={handleShowPostDetails}
          params={params}
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
      </PageLayout>

      {selectedPost && (
        <PostDetails
          onClose={() => {
            setSelectedPostId(null);
            detailsDialog.close();
          }}
          onOpenChange={detailsDialog.setIsOpen}
          open={detailsDialog.isOpen}
          post={selectedPost}
        />
      )}

      {!!user && selectedPost && (
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

      {!!user && selectedPostId && (
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

export default Community;
