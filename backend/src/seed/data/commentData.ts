import { CommentType } from "../../constants/commentType";
import Comment from "../../models/commentModel";
export const seedComments = async () => {
  await Comment.deleteMany();
  await Comment.insertMany(comments);
  console.log("Komentarze dodane");
};

const comments = [
  {
    _id: "6888da1a43fc6c0b89e26606",
    content: "Świetne ćwiczenie!",
    author: "68875a0b053920a2eca69cbe",
    likes: 1,
    parentType: CommentType.Exercise,
    parentId: "68875afe9d9533738f442263",
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: "6888da3e88ab14770092a2ec",
    content: "Zgadzam się z Moniką!",
    author: "6887cc687882c42182cdf1f5",
    parentType: CommentType.Comment,
    parentId: "6888da1a43fc6c0b89e26606",
  },
];
