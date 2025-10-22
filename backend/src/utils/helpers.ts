import { CommentParentType } from '../constants/commentType';
import Comment from '../models/commentModel';
import User from '../models/userModel';

export const deleteCommentsAndLikes = async (parentType: CommentParentType, parentId: string) => {
  const comments = await Comment.find({
    parentType,
    parentId,
  }).select('_id');

  const commentIds = comments.map((comment) => comment._id);

  const replies = await Comment.find({
    parentType: CommentParentType.comment,
    parentId: { $in: commentIds },
  }).select('_id');

  const replyIds = replies.map((reply) => reply._id);

  const allCommentIds = [...commentIds, ...replyIds];

  if (allCommentIds.length > 0) {
    await User.updateMany(
      { 'likes.comments': { $in: allCommentIds } },
      { $pull: { 'likes.comments': { $in: allCommentIds } } },
    );
  }

  if (replyIds.length > 0) {
    await Comment.deleteMany({
      _id: { $in: replyIds },
    });
  }

  if (commentIds.length > 0) {
    await Comment.deleteMany({
      _id: { $in: commentIds },
    });
  }
};
