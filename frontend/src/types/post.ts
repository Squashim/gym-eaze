import type { Author } from './user';

type BasePost<TImage = string, TVideo = string> = {
  content: string;
  image?: TImage;
  video?: TVideo;
  hashtags?: string[];
};

export type CreatePostPayload = BasePost<File | null, File | null>;

export type UpdatePostPayload = Partial<CreatePostPayload>;

export type GetPostsApiResponse = BasePost & {
  _id: string;
  author: Author;
  likes: number;
  commentsCount?: number;
  isEdited?: boolean;
  createdAt: string;
  updatedAt: string;
};
