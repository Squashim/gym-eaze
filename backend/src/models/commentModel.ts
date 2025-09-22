import mongoose, { Types } from 'mongoose';
import { CommentParentType } from '../constants/commentType';

export interface IComment extends mongoose.Document {
  content: string;
  author: Types.ObjectId;
  likes: number;
  parentType: CommentParentType;
  parentId: Types.ObjectId;
  level: number; // 0 = główny komentarz, 1 = odpowiedź na komentarz
  isDeleted: boolean; // soft delete
  createdAt: Date;
  updatedAt: Date;
}

const commentSchema = new mongoose.Schema(
  {
    content: {
      type: String,
      required: function (this: IComment) {
        return !this.isDeleted; // content nie jest wymagany dla usuniętych komentarzy
      },
    },
    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    likes: {
      type: Number,
      default: 0,
    },
    parentType: {
      type: String,
      enum: Object.values(CommentParentType),
      required: true,
    },
    parentId: {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
    },
    level: {
      type: Number,
      default: 0,
      min: 0,
      max: 1,
    },
    isDeleted: {
      type: Boolean,
      default: false,
    },
  },
  {
    timestamps: true,
    // dla lepszej wydajności
    index: [{ parentType: 1, parentId: 1 }, { level: 1 }, { createdAt: -1 }],
  },
);

commentSchema.pre('save', async function (this: IComment) {
  if (this.parentType === CommentParentType.comment) {
    const parentComment = await Comment.findById(this.parentId);
    if (!parentComment) throw new Error('Komentarz nadrzędny nie istnieje');
    if (parentComment.level >= 1)
      throw new Error('Maksymalny poziom zagnieżdżenia to 2 (komentarz -> odpowiedź)');
    this.level = parentComment.level + 1;
  }
});

commentSchema.pre('findOneAndUpdate', async function () {
  const update = this.getUpdate() as Record<string, unknown>;
  if (update.isDeleted === true) {
    const comment = await Comment.findOne(this.getQuery());
    if (comment && comment.level === 0) {
      await Comment.updateMany(
        { parentType: CommentParentType.comment, parentId: comment._id },
        { isDeleted: true, content: '[Usunięto]' },
      );
    }
  }
});

const Comment = mongoose.model<IComment>('Comment', commentSchema);
export default Comment;
