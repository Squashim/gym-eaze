import PostCreator from '@/components/features/posts/post-creator';
import { EditPostDialog } from '@/components/features/posts/post-dialogs';
import PostFilters from '@/components/features/posts/post-filters';
import PostList from '@/components/features/posts/post-list';
import PageLayout from '@/components/layout/page';
import { Alert, AlertTitle } from '@/components/ui/alert';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import { useUser } from '@/context/user';
import useGetPosts from '@/hooks/query/posts/use-get-posts';
import { usePost } from '@/hooks/query/posts/use-post';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import usePostParamsQuery from '@/hooks/use-post-params-query';
import useOpen from '@/hooks/utils/use-open';
import usePageTitle from '@/hooks/utils/use-page-title';
import { Info, Loader2 } from 'lucide-react';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import { toast } from 'sonner';

function Community() {
  const { params, setHashtags, setParam, clearParams } = usePostParamsQuery();

  const { user } = useUser();
  const deleteDialog = useOpen();
  const editDialog = useOpen();
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
  const prevCountRef = useRef<number>(0);
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

  useEffect(() => {
    if (prevCountRef.current && posts.length > prevCountRef.current) {
      toast.info('Dostępne są nowe posty.', {
        duration: 5000,
        action: {
          label: 'Zobacz',
          onClick: () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
          },
        },
      });
    }
    prevCountRef.current = posts.length;
  }, [posts.length]);

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

  const handlePostDelete = useCallback(
    (postId: string) => {
      setSelectedPostId(postId);
      deleteDialog.open();
    },
    [deleteDialog],
  );

  const handleConfirmDelete = () => {
    if (selectedPostId) {
      deletePost.mutate(selectedPostId, {
        onSuccess: () => {
          prevCountRef.current = Math.max(0, prevCountRef.current - 1);
        },
      });
    }
  };

  const renderPostLoader = () => {
    if (!isFetchingNextPage) {
      return (
        <>
          {hasNextPage && posts.length > 0 && <div className="h-2" ref={ref} />}
          {!hasNextPage && posts.length > 0 && (
            <Alert>
              <Info />
              <AlertTitle>Brak kolejnych postów.</AlertTitle>
            </Alert>
          )}
        </>
      );
    }

    return (
      <Alert>
        <Loader2 className="animate-spin" />
        <AlertTitle>Ładowanie kolejnych postów...</AlertTitle>
      </Alert>
    );
  };

  return (
    <>
      <PageLayout className="max-w-xl sm:max-w-2xl" title="Posty">
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
          params={params}
          posts={posts}
        />
        {renderPostLoader()}
        <ScrollToTopButton />
      </PageLayout>

      {!!user && selectedPost && (
        <EditPostDialog
          onClose={editDialog.close}
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
          onClose={deleteDialog.close}
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
