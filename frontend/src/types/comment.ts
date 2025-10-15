import type { ItemType } from '@/constants/enums/item-type';
import type { Author } from './user';

type BaseComment = {
  content: string;
};

export type CreateCommentPayload = BaseComment & {
  parentType: ItemType;
  parentId: string;
};

export type UpdateCommentPayload = Partial<BaseComment>;

export type GetCommentsApiResponse = BaseComment & {
  _id: string;
  author: Author;
  likes: number;
  repliesCount?: number;
  isEdited?: boolean;
  createdAt: string;
  updatedAt: string;
};
