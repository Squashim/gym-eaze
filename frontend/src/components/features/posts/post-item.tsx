import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Separator } from '@/components/ui/separator';
import Typography from '@/components/ui/typography';
import { parseTimeAgo } from '@/lib/parsers';
import type { GetPostsApiResponse } from '@/types/post';
import { useCallback } from 'react';
import PostManageDropdown from './post-manage-dropdown';
import PostMediaPreview from './post-media-preview';
import PostLike from './post-like';
import { MessageSquare } from 'lucide-react';

interface PostItemProps {
  post: GetPostsApiResponse;
  auth: {
    isLogged: boolean;
    isAuthor: boolean;
  };
  like: {
    isLiked: boolean;
    isLikedPostsLoading: boolean;
    isLikedPostsError: boolean;
    isLikePending: boolean;
  };
  onDelete: (postId: string) => void;
  onEdit: (postId: string) => void;
  onLikeToggle: (postId: string) => void;
}

function PostItem({ post, auth, like, onDelete, onEdit, onLikeToggle }: PostItemProps) {
  const handlePostDelete = useCallback(() => {
    onDelete(post._id);
  }, [onDelete, post._id]);

  const handlePostEdit = useCallback(() => {
    onEdit(post._id);
  }, [onEdit, post._id]);

  const handleToggleLike = useCallback(() => {
    onLikeToggle(post._id);
  }, [onLikeToggle, post._id]);

  return (
    <div className="bg-card text-card-foreground rounded-xl border p-4 shadow-sm flex flex-col gap-2 w-full">
      <header className="flex gap-4 justify-between items-center w-full">
        <div className="flex gap-2 items-center">
          <Button asChild className="size-10 rounded-3xl p-0" variant="ghost">
            <a
              className="hover:ring-ring/50 hover:ring-[3px] rounded-3xl"
              href={`/profile/${post.author._id}`}
              rel="noreferrer"
              target="_blank"
            >
              <ReusableAvatar
                className="cursor-pointer shadow-sm size-10"
                fallbackClassName="dark:bg-background"
                profilePicture={post.author.profileDetails.profilePicture}
                username={post.author.username}
              />
            </a>
          </Button>
          <div className="flex flex-col">
            <Button asChild className="p-0 justify-start w-fit h-fit" variant="link">
              <a href={`/profile/${post.author._id}`} rel="noreferrer" target="_blank">
                <Typography className="font-semibold">
                  {post.author.username} {auth.isAuthor && '(Ty)'}
                </Typography>
              </a>
            </Button>

            <Typography className="text-sm" variant="muted">
              {parseTimeAgo(post.createdAt)}
            </Typography>
          </div>
        </div>
        {auth.isLogged && (
          <PostManageDropdown
            isAuthor={auth.isAuthor}
            onDelete={handlePostDelete}
            onEdit={handlePostEdit}
          />
        )}
      </header>
      <Typography className="break-words whitespace-break-spaces max-h-50 overflow-y-auto scrollbar">
        {post.content}
      </Typography>

      {post.image && (
        <PostMediaPreview author={post.author.username} src={post.image} type="image" />
      )}

      {post.video && (
        <PostMediaPreview author={post.author.username} src={post.video} type="video" />
      )}

      {post.hashtags && post.hashtags.length > 0 && (
        <div className="flex flex-nowrap gap-2 overflow-y-auto scrollbar px-1 py-2">
          {post.hashtags.map((hashtag) => (
            <Badge className="px-3 py-1.5 rounded-md" key={hashtag} variant="outline">
              {`#${hashtag}`}
            </Badge>
          ))}
        </div>
      )}

      <div className="flex flex-col justify-center min-[500px]:flex-row gap-2 mt-2 max-[500px]:items-center">
        <PostLike
          isLiked={like.isLiked}
          isLikedPostsError={like.isLikedPostsError}
          isLikedPostsLoading={like.isLikedPostsLoading}
          isLogged={auth.isLogged}
          isPending={like.isLikePending}
          likesCount={post.likes}
          onToggleLike={handleToggleLike}
        />

        <Separator
          className="bg-primary/20 rounded-md hidden min-[500px]:block"
          orientation="vertical"
        />

        <Button className="w-full flex-1/2" variant="ghost">
          <MessageSquare className="size-5" />
          Zobacz komentarze: 0
        </Button>
      </div>
    </div>
  );
}

export default PostItem;
