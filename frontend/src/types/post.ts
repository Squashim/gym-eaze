export type CreatePostPayload = {
  content: string;
  image?: File | null;
  video?: File | null;
  hashtags?: string[];
};
