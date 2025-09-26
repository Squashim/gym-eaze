import mongoose, { Types } from 'mongoose';
import { CommentParentType, commentParentTypeValues } from '../constants/commentType';

export interface IComment extends mongoose.Document {
  content: string;
  author: Types.ObjectId;
  likes: number;
  parentType: CommentParentType;
  parentId: Types.ObjectId;
  isEdited?: boolean;
  repliesCount?: number;
  createdAt: Date;
  updatedAt: Date;
}

const commentSchema = new mongoose.Schema(
  {
    content: {
      type: String,
      required: true,
      validate: {
        validator: function (v: string) {
          const normalized = v.replace(/\r\n/g, '\n');
          return normalized.length <= 1000;
        },
        message: 'Treść może mieć maksymalnie 1000 znaków',
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
      enum: commentParentTypeValues,
      required: true,
    },
    parentId: {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
    },
    isEdited: {
      type: Boolean,
    },
    repliesCount: {
      type: Number,
    },
  },
  {
    timestamps: true,
    index: [{ parentType: 1, parentId: 1 }, { createdAt: -1 }],
  },
);

commentSchema.pre('save', async function (this: IComment) {
  if (this.parentType === CommentParentType.comment) {
    const parentComment = await Comment.findById(this.parentId);
    if (!parentComment) throw new Error('Komentarz nadrzędny nie istnieje');
    if (parentComment.parentType === CommentParentType.comment)
      throw new Error('Maksymalny poziom zagnieżdżenia to 2 (komentarz -> odpowiedź)');
  }
});

const Comment = mongoose.model<IComment>('Comment', commentSchema);
export default Comment;
