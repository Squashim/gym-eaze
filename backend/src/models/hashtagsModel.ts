import mongoose from "mongoose";

export interface IHashtag extends mongoose.Document {
  name: string;
  createdAt: Date;
}
// to do wywalenia chyba calkiem
const hashtagsSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

const Hashtag = mongoose.model<IHashtag>("Hashtag", hashtagsSchema);

export default Hashtag;
