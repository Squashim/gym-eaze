import mongoose from "mongoose";

export interface IHashtag extends mongoose.Document {
  name: string;
  description?: string;
  category?: string;
  usageCount: number;
  createdAt: Date;
  updatedAt: Date;
}

const hashtagSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
      unique: true,
      maxLength: 30,
      minLength: 2,
    },
    description: {
      type: String,
      maxLength: 200,
    },
    category: {
      type: String,
      maxLength: 50,
    },
    usageCount: {
      type: Number,
      default: 0,
      min: 0,
    },
  },
  { timestamps: true }
);

const Hashtag = mongoose.model<IHashtag>("Hashtag", hashtagSchema);

export default Hashtag;