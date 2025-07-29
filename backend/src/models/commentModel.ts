import mongoose, { Types } from "mongoose";
import { CommentType } from "../constants/commentType";

interface IComment extends mongoose.Document {
  content: string;
  author: Types.ObjectId;
  likes: number;
  parentType: CommentType;
  parentId: Types.ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const commentSchema = new mongoose.Schema(
  {
    content: {
      type: String,
      required: true,
    },

    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },

    likes: {
      type: Number,
      default: 0,
    },

    parentType: {
      type: String,
      enum: CommentType,
    },

    parentId: {
      type: mongoose.Schema.Types.ObjectId,
    },
  },
  { timestamps: true },
);

const Comment = mongoose.model<IComment>("Comment", commentSchema);

export default Comment;
