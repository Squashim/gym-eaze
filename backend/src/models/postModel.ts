import mongoose, { Types } from 'mongoose';
import { IUser } from './userModel';

export interface IPost extends mongoose.Document {
  author: Types.ObjectId | IUser;
  content: string;
  image?: string;
  video?: string;
  hashtags?: string[];
  likes: number;
  commentsCount?: number;
  createdAt: Date;
  updatedAt: Date;
}

const postSchema = new mongoose.Schema(
  {
    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    content: {
      type: String,
      maxlength: 800,
      required: true,
    },
    image: {
      type: String,
    },
    video: {
      type: String,
    },
    hashtags: [
      {
        type: String,
        maxLength: 35,
      },
    ],
    likes: {
      type: Number,
      default: 0,
    },
    commentsCount: {
      type: Number,
    },
  },
  { timestamps: true },
);

const Post = mongoose.model<IPost>('Post', postSchema);

export default Post;
