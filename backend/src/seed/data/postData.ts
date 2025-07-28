import Post from "../../models/postModel";

export const seedPosts = async () => {
  await Post.deleteMany();
  await Post.insertMany(posts);
  console.log("Posty dodane");
};

const posts = [
  {
    _id: "6887ce64297974f8c21a1fcf",
    title: "Mój pierwszy post",
    content: "To jest mój pierwszy post na GymEaze!",
    image: "https://ibb.co/rGhC5QYV",
    createdAt: new Date(),
    updatedAt: new Date(),
    authorId: "68875a0b053920a2eca69cbe", // monikaGym
  },
];
