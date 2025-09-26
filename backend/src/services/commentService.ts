import { Types, FilterQuery } from 'mongoose';
import CommentModel from '../models/commentModel';
import {
  AllParentTypes,
  CommentParentType,
  MainCommentParentTypes,
} from '../constants/commentType';

export const createCommentService = async (
  content: string,
  parentType: AllParentTypes,
  parentId: string,
  authorId: string,
) => {
  const isReply = parentType === CommentParentType.comment;

  if (!Types.ObjectId.isValid(parentId)) {
    throw new Error('Nieprawidłowy identyfikator rodzica');
  }

  if (!Types.ObjectId.isValid(authorId)) {
    throw new Error('Nieprawidłowy identyfikator użytkownika');
  }

  if (isReply) {
    const parentComment = await CommentModel.findById(parentId);

    if (!parentComment || parentComment.parentType === CommentParentType.comment) {
      throw new Error('Zbyt głęboko zagnieżdżony komentarz');
    }
  }

  const comment = new CommentModel({
    content,
    author: new Types.ObjectId(authorId),
    parentType: parentType,
    parentId: new Types.ObjectId(parentId),
    repliesCount: isReply ? undefined : 0,
  });

  if (isReply) {
    await CommentModel.findByIdAndUpdate(parentId, { $inc: { repliesCount: 1 } });
  } else {
    switch (parentType) {
      case CommentParentType.exercise:
        //TODO:
        break;
      case CommentParentType.training:
        //TODO:
        break;
      case CommentParentType.post:
        //TODO:
        break;
    }
  }

  await comment.save();
};

export const getCommentsWithCursor = async (
  parentType: MainCommentParentTypes,
  parentId: string,
  cursor?: string,
  limit: number = 10,
) => {
  if (!Types.ObjectId.isValid(parentId)) {
    throw new Error('Nieprawidłowy identyfikator rodzica');
  }

  const query: FilterQuery<typeof CommentModel.schema> = {
    parentType: parentType,
    parentId: new Types.ObjectId(parentId),
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
    items,
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
    throw new Error('Nieprawidłowy identyfikator komentarza');
  }

  const comment = await CommentModel.findById(commentId);
  if (!comment || comment.parentType !== CommentParentType.comment) {
    throw new Error('Komentarz nie istnieje lub nie można pobrać odpowiedzi');
  }

  const query: FilterQuery<typeof CommentModel.schema> = {
    parentType: CommentParentType.comment,
    parentId: new Types.ObjectId(commentId),
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
    items,
    hasMore,
    nextCursor,
  };
};
