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
  params?: PostQueryParams;
  isLoading: boolean;
  isError: boolean;
  likedPosts: {
    data?: Set<string>;
    isLoading: boolean;
    isError: boolean;
  };
  likePendingId: string | null;
  onDelete: (postId: string) => void;
  onEdit: (postId: string) => void;
  onReport: (postId: string) => void;
  onLikeToggle: (postId: string) => void;
  onShowDetails: (postId: string) => void;
}

function PostList({
  posts,
  params,
  isLoading,
  isError,
  likedPosts,
  likePendingId,
  onDelete,
  onEdit,
  onReport,
  onLikeToggle,
  onShowDetails,
}: PostListProps) {
  const { user } = useUser();

  const isLiked = useMemo(() => {
    return (postId: string) => likedPosts.data?.has(postId) ?? false;
  }, [likedPosts]);

  const isAuthor = useMemo(() => {
    return (authorId: string) => !!user && user._id === authorId;
  }, [user]);

  const isLikePending = useMemo(() => {
    return (postId: string) => likePendingId === postId;
  }, [likePendingId]);

  if (isLoading) {
    return (
      <div className="flex flex-col gap-4 w-full">
        {Array.from({ length: 6 }).map((_, index) => (
          <Skeleton className="h-42 w-full rounded-xl" key={index} />
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4 w-full">
        <Alert>
          <Info />
          <AlertTitle>Wystąpił błąd podczas pobierania postów. Spróbuj ponownie</AlertTitle>
        </Alert>
      </div>
    );
  }

  if (posts && posts.length === 0) {
    return (
      <div className="flex flex-col gap-4 w-full">
        <Alert>
          <Info />
          <AlertTitle>
            {params && (params.followedOnly !== undefined || params.hashtags?.length)
              ? 'Nie znaleziono postów spełniających podane kryteria.'
              : params
                ? 'Brak postów. Dodaj pierwszego posta!'
                : 'Użytkownik nie dodał jeszcze żadnego posta.'}
          </AlertTitle>
        </Alert>
      </div>
    );
  }

  return (
    <>
      <div className="flex flex-col gap-4 w-full">
        {posts!.map((post) => (
          <PostItem
            auth={{
              isLogged: !!user,
              isAuthor: isAuthor(post.author._id),
            }}
            key={post._id}
            like={{
              isLiked: isLiked(post._id),
              isLikedPostsLoading: likedPosts.isLoading,
              isLikedPostsError: likedPosts.isError,
              isLikePending: isLikePending(post._id),
            }}
            onDelete={onDelete}
            onEdit={onEdit}
            onLikeToggle={onLikeToggle}
            onReport={onReport}
            onShowDetails={onShowDetails}
            post={post}
          />
        ))}
      </div>
    </>
  );
}

export default PostList;
