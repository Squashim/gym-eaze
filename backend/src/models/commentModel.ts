import mongoose from "mongoose";

interface IComment extends mongoose.Document {
  content: string;
  author: mongoose.Schema.Types.ObjectId;
  likes: number;
  // to mozna do odzielnego typu dac
  parentType: "Exercise" | "Training" | "Post" | "Comment";
  parentId: mongoose.Schema.Types.ObjectId;
  replies: mongoose.Schema.Types.ObjectId[];
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
      enum: ["Exercise", "Training", "Post"],
    },

    parentId: {
      type: mongoose.Schema.Types.ObjectId,
    },
  },
  { timestamps: true },
);

const Comment = mongoose.model<IComment>("Comment", commentSchema);

export default Comment;
