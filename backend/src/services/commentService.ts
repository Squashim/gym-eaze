import { Types, FilterQuery } from 'mongoose';
import CommentModel from '../models/commentModel';
import { CommentParentType } from '../constants/commentType';

export const createCommentService = async (
  content: string,
  parentType: 'Post' | 'Ćwiczenie' | 'Trening',
  parentId: string,
  authorId: string,
) => {
  if (content.length > 1000) {
    throw new Error('Komentarz nie może być dłuższy niż 1000 znaków');
  }

  if (!Types.ObjectId.isValid(parentId)) {
    throw new Error('Nieprawidłowy ID rodzica');
  }

  if (!Types.ObjectId.isValid(authorId)) {
    throw new Error('Nieprawidłowy ID użytkownika');
  }

  const parentTypeEnum = parentType as CommentParentType;

  const comment = new CommentModel({
    content,
    author: new Types.ObjectId(authorId),
    parentType: parentTypeEnum,
    parentId: new Types.ObjectId(parentId),
    level: 0, // główny komentarz
    likes: 0,
    isDeleted: false,
  });

  const savedComment = await comment.save();

  return savedComment;
};

export const createReplyService = async (content: string, parentId: string, authorId: string) => {
  const parentComment = await CommentModel.findById(parentId);
  if (!parentComment || parentComment.isDeleted) {
    throw new Error('Komentarz nadrzędny nie istnieje');
  }
  if (parentComment.level >= 1) {
    throw new Error('Nie można odpowiadać na odpowiedzi');
  }

  const reply = new CommentModel({
    content,
    author: new Types.ObjectId(authorId),
    parentType: CommentParentType.comment,
    parentId: parentId,
    level: 1,
    likes: 0,
    isDeleted: false,
  });

  return await reply.save();
};

export const getMainCommentsWithCursor = async (
  parentType: 'Post' | 'Ćwiczenie' | 'Trening',
  parentId: string,
  cursor?: string,
  limit: number = 10,
) => {
  if (!Types.ObjectId.isValid(parentId)) {
    throw new Error('Nieprawidłowy ID rodzica');
  }

  const query: FilterQuery<typeof CommentModel.schema> = {
    parentType: parentType as CommentParentType,
    parentId: new Types.ObjectId(parentId),
    level: 0,
    isDeleted: false,
  };

  if (cursor) {
    query._id = { $lt: new Types.ObjectId(cursor) };
  }

  const items = await CommentModel.find(query)
    .sort({ createdAt: -1, _id: -1 })
    .limit(limit + 1)
    .populate('author', 'username profileDetails.profilePicture')
    .lean();

  const hasMore = items.length > limit;
  if (hasMore) {
    items.pop();
  }

  const nextCursor = hasMore && items.length > 0 ? items[items.length - 1]._id.toString() : null;

  const commentsWithReplyCount = await Promise.all(
    items.slice(0, limit).map(async (comment) => {
      const replyCount = await CommentModel.countDocuments({
        parentType: CommentParentType.comment,
        parentId: comment._id,
        level: 1,
        isDeleted: false,
      });

      return {
        ...comment,
        replyCount,
      };
    }),
  );

  return {
    items: commentsWithReplyCount,
    hasMore,
    nextCursor,
  };
};

export const getRepliesForComment = async (
  commentId: string,
  cursor?: string,
  limit: number = 10,
) => {
  if (!Types.ObjectId.isValid(commentId)) {
    throw new Error('Nieprawidłowy ID komentarza');
  }

  const parentComment = await CommentModel.findById(commentId);
  if (!parentComment || parentComment.isDeleted || parentComment.level !== 0) {
    throw new Error('Komentarz nadrzędny nie istnieje lub nie można pobrać odpowiedzi');
  }

  const query: FilterQuery<typeof CommentModel.schema> = {
    parentType: CommentParentType.comment,
    parentId: new Types.ObjectId(commentId),
    level: 1,
    isDeleted: false,
  };

  if (cursor) {
    query._id = { $lt: new Types.ObjectId(cursor) };
  }

  const items = await CommentModel.find(query)
    .sort({ createdAt: -1, _id: -1 })
    .limit(limit + 1)
    .populate('author', 'username profileDetails.profilePicture')
    .lean();

  const hasMore = items.length > limit;
  if (hasMore) {
    items.pop();
  }

  const nextCursor = hasMore && items.length > 0 ? items[items.length - 1]._id.toString() : null;

  return {
    items: items.slice(0, limit),
    hasMore,
    nextCursor,
  };
};
