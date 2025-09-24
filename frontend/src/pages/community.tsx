import PostCreator from '@/components/features/posts/post-creator';
import PostList from '@/components/features/posts/post-list';
import PageLayout from '@/components/layout/page';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Switch } from '@/components/ui/switch';
import { TagsInput } from '@/components/ui/tags-input';
import Typography from '@/components/ui/typography';
import { useUser } from '@/context/user';
import useGetPosts from '@/hooks/query/posts/use-get-posts';
import useGetLikedItems from '@/hooks/query/user/use-get-liked-items';
import usePostParamsQuery from '@/hooks/use-post-params-query';
import { Info, Loader2 } from 'lucide-react';
import { useEffect } from 'react';
import { useInView } from 'react-intersection-observer';

function Community() {
  const { params, setHashtags, setParam } = usePostParamsQuery();
  const { user } = useUser();
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

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage && !isLoading) {
      void fetchNextPage();
    }
  }, [inView, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading]);

  const posts = data?.pages.flatMap((page) => page.items) ?? [];

  return (
    <PageLayout title="Posty">
      {user && <PostCreator />}

      <div className="grid gap-2">
        <Typography className="font-semibold" variant="small">
          Wyszukaj po hasztagach:
        </Typography>
        <TagsInput
          id="post-search-hashtags"
          onChange={(tags) => setHashtags(tags)}
          placeholder="Podaj hasztagi, np. biceps, triceps - użyj Entera lub przecinka"
          value={params.hashtags || []}
        />
      </div>

      <div className="grid gap-2">
        <Typography className="font-semibold" variant="small">
          Pokaż posty obserwowanych użytkowników:
        </Typography>
        <div className="flex flex-row items-center justify-between rounded-md border border-input p-3 bg-transparent dark:bg-input/30 shadow-xs">
          <div className="grid gap-1">
            <Typography variant="muted">
              Włącz, aby wyświetlać tylko posty osób, które obserwujesz
            </Typography>
          </div>
          <Switch
            checked={params.followedOnly === true}
            onCheckedChange={(checked) => setParam('followedOnly', checked)}
          />
        </div>
      </div>

      <PostList
        isError={isError}
        isLoading={isLoading}
        likedPosts={{
          data: likedPosts,
          isLoading: isLikedPostsLoading,
          isError: isLikesPostsError,
        }}
        params={params}
        posts={posts}
      />

      {!isFetchingNextPage ? (
        <>
          {hasNextPage && posts.length > 0 && <div className="h-2" ref={ref} />}
          {!hasNextPage && posts.length > 0 && (
            <Alert>
              <Info />
              <AlertTitle>Brak kolejnych postów.</AlertTitle>
            </Alert>
          )}
        </>
      ) : (
        <Alert>
          <Loader2 className="animate-spin" />
          <AlertTitle>Ładowanie kolejnych postów...</AlertTitle>
        </Alert>
      )}
    </PageLayout>
  );
}

export default Community;
