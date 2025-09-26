import { Types, FilterQuery } from 'mongoose';
import CommentModel from '../models/commentModel';
import Exercise from '../models/exerciseModel';
import TrainingPlan from '../models/trainingPlanModel';
import Post from '../models/postModel';
import {
  AllParentTypes,
  CommentParentType,
  MainCommentParentTypes,
} from '../constants/commentType';
import User from '../models/userModel';

export const createCommentService = async (
  content: string,
  parentType: AllParentTypes,
  parentId: string,
  authorId: string,
) => {
  const isReply = parentType === CommentParentType.comment;

  if (!Types.ObjectId.isValid(parentId)) {
    throw new Error(
      'Nieprawidłowy identyfikator rodzica - (Ćwiczenie | Trening | Post | Komentarz)',
    );
  }

  if (!Types.ObjectId.isValid(authorId)) {
    throw new Error('Nieprawidłowy identyfikator użytkownika');
  }

  if (!isReply) {
    let parentExists = false;
    switch (parentType) {
      case CommentParentType.exercise:
        parentExists = !!(await Exercise.exists({ _id: parentId }));
        break;
      case CommentParentType.training:
        parentExists = !!(await TrainingPlan.exists({ _id: parentId }));
        break;
      case CommentParentType.post:
        parentExists = !!(await Post.exists({ _id: parentId }));
        break;
    }
    if (!parentExists) {
      throw new Error('Nie znaleziono rodzica dla podanego typu');
    }
  } else {
    const parentComment = await CommentModel.findById(parentId);

    if (!parentComment) {
      throw new Error('Nie znaleziono rodzica dla podanego typu');
    }

    if (parentComment.parentType === CommentParentType.comment) {
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
        await Exercise.findByIdAndUpdate(parentId, { $inc: { commentsCount: 1 } });
        break;
      case CommentParentType.training:
        await TrainingPlan.findByIdAndUpdate(parentId, { $inc: { commentsCount: 1 } });
        break;
      case CommentParentType.post:
        await Post.findByIdAndUpdate(parentId, { $inc: { commentsCount: 1 } });
        break;
    }
  }

  await comment.save();
};

export const updateCommentService = async (commentId: string, userId: string, content: string) => {
  if (!Types.ObjectId.isValid(commentId)) {
    throw new Error('Nieprawidłowy identyfikator komentarza/odpowiedzi');
  }

  if (!Types.ObjectId.isValid(userId)) {
    throw new Error('Nieprawidłowy identyfikator użytkownika');
  }

  const comment = await CommentModel.findById(commentId);

  if (!comment) {
    throw new Error('Komentarz/odpowiedź nie istnieje');
  }

  if (comment.author.toString() !== userId) {
    throw new Error('Brak uprawnień do edytowania tego komentarza/odpowiedzi');
  }

  comment.content = content.trim();
  comment.isEdited = true;

  await comment.save();

  return comment.parentType === CommentParentType.comment
    ? 'Odpowiedź została zaktualizowana'
    : 'Komentarz został zaktualizowany';
};

export const deleteCommentService = async (commentId: string, userId: string) => {
  if (!Types.ObjectId.isValid(commentId)) {
    throw new Error('Nieprawidłowy identyfikator komentarza/odpowiedzi');
  }

  if (!Types.ObjectId.isValid(userId)) {
    throw new Error('Nieprawidłowy identyfikator użytkownika');
  }

  const comment = await CommentModel.findById(commentId);

  if (!comment) {
    throw new Error('Komentarz/odpowiedź nie istnieje');
  }

  const isReply = comment.parentType === CommentParentType.comment;

  if (comment.author.toString() !== userId) {
    throw new Error('Brak uprawnień do usunięcia tego komentarza/odpowiedzi');
  }

  if (comment.parentType === CommentParentType.comment) {
    await CommentModel.findByIdAndUpdate(comment.parentId, { $inc: { repliesCount: -1 } });
  } else {
    switch (comment.parentType) {
      case CommentParentType.exercise:
        await Exercise.findByIdAndUpdate(comment.parentId, { $inc: { commentsCount: -1 } });
        break;
      case CommentParentType.training:
        await TrainingPlan.findByIdAndUpdate(comment.parentId, { $inc: { commentsCount: -1 } });
        break;
      case CommentParentType.post:
        await Post.findByIdAndUpdate(comment.parentId, { $inc: { commentsCount: -1 } });
        break;
    }

    await CommentModel.deleteMany({ parentType: CommentParentType.comment, parentId: comment._id });
  }

  await comment.deleteOne();

  return isReply ? 'Odpowiedź została usunięta' : 'Komentarz został usunięty';
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
    .sort({ createdAt: -1, likes: -1, _id: -1 })
    .limit(limit + 1)
    .populate('author', 'username profileDetails.profilePicture')
    .select('-parentType -parentId')
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
  if (!comment || comment.parentType === CommentParentType.comment) {
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
    .sort({ createdAt: -1, likes: -1, _id: -1 })
    .limit(limit + 1)
    .populate('author', 'username profileDetails.profilePicture')
    .select('-parentType -parentId')
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

export const toggleCommentLikeService = async (userId: string, commentId: string) => {
  if (!Types.ObjectId.isValid(commentId)) {
    throw new Error('Nieprawidłowy identyfikator komentarza');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const comment = await CommentModel.findById(commentId);
  if (!comment) {
    throw new Error('Nie znaleziono komentarza/odpowiedzi');
  }

  const isLiked = user.likes.comments.some((fav) => fav.toString() === commentId);

  if (isLiked) {
    user.likes.comments = user.likes.comments.filter((fav) => fav.toString() !== commentId);
    comment.likes = Math.max(0, (comment.likes || 0) - 1);
    await user.save();
    await comment.save();
    return { message: 'Usunięto polubienie', likes: comment.likes };
  } else {
    user.likes.exercises.push(comment._id as Types.ObjectId);
    comment.likes = (comment.likes || 0) + 1;
    await user.save();
    await comment.save();
    return { message: 'Dodano polubienie', likes: comment.likes };
  }
};
