import type { GetPostsApiResponse } from '@/types/post';
import PostItem from './post-item';
import { useUser } from '@/context/user';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Info } from 'lucide-react';
import { useMemo } from 'react';
import type { PostQueryParams } from '@/types/filters';

interface PostListProps {
  posts: GetPostsApiResponse[] | undefined;
  isLoading: boolean;
  isError: boolean;
  likedPosts: {
    data?: Set<string>;
    isLoading: boolean;
    isError: boolean;
  };
  params: PostQueryParams;
}

function PostList({ posts, isLoading, isError, likedPosts, params }: PostListProps) {
  const { user } = useUser();

  const isLiked = useMemo(() => {
    return (postId: string) => likedPosts.data?.has(postId) ?? false;
  }, [likedPosts]);

  const isAuthor = useMemo(() => {
    return (authorId: string) => user?._id === authorId;
  }, [user]);

  if (isLoading) {
    return (
      <div className="flex flex-col gap-4 max-w-xl">
        {Array.from({ length: 6 }).map((_, index) => (
          <Skeleton className="h-42 w-full rounded-xl" key={index} />
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4 max-w-xl">
        <Alert>
          <Info />
          <AlertTitle>Wystąpił błąd podczas pobierania postów. Spróbuj ponownie</AlertTitle>
        </Alert>
      </div>
    );
  }

  if (posts && posts.length === 0) {
    return (
      <div className="flex flex-col gap-4 max-w-xl">
        <Alert>
          <Info />
          <AlertTitle>
            {params.followedOnly !== undefined || params.hashtags?.length
              ? 'Nie znaleziono postów spełniających podane kryteria.'
              : 'Brak postów. Dodaj pierwszego posta!'}
          </AlertTitle>
        </Alert>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-4">
      {posts!.map((post) => (
        <PostItem
          isAuthor={isAuthor(post.author._id)}
          isLiked={isLiked(post._id)}
          isLikedPostsError={likedPosts.isError}
          isLikedPostsLoading={likedPosts.isLoading}
          isLogged={!!user}
          key={post._id}
          post={post}
        />
      ))}
    </div>
  );
}

export default PostList;
