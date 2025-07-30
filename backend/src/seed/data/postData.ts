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
    likes: 0,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "68875a0b053920a2eca69cbe", // monikaGym
  },
  {
    _id: "6888b879a8fb0f97919d1483",
    title: "Jestesmy w mediolaaanie!",
    content:
      "Dzisiaj spedziłam cały dzień na plaży i zrobiłam świetne zdjęcia!",
    likes: 12,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "68875a0b053920a2eca69cbe", // monikaGym
  },
  {
    _id: "6888c76f748cd4e6333bb566",
    title: "Moc i energia na treningu",
    content:
      "Dzisiaj miałem niesamowity trening siłowy! Czuję się pełen energii i gotowy na kolejne wyzwania.",
    image: "https://ibb.co/1GP8VRZz",
    likes: 4,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "6887c5f43118c221f43b4e23", // fitnessFan
  },
  {
    _id: "6888c8245a5a041ce494f1a2",
    title: "Trening z chlopakami",
    content: "Ziomale jak zwykle dali czadu na treningu! 💪🔥",
    image: "https://ibb.co/CKzVGNJb",
    likes: 8,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "6887c5f43118c221f43b4e23", // fitnessFan
  },
  {
    _id: "6888c8a87060ee65c1ed0401",
    title: "Maz nagral filmik",
    content:
      "Wyrwal mi telefon i powiedział, że to z przekazem dla wszystkich! 😂",
    video: "https://streamable.com/dh4tm8",
    likes: 5,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "6887cc687882c42182cdf1f5", // paulaRunner
  },
  {
    _id: "6888c8ae472693a95401bf84",
    title: "Nowy rekord w biegu!",
    content:
      "Dzisiaj pobiłam swój rekord na 10 km! Jestem taka szczęśliwa! 🏃‍♀️💨",
    image: "https://ibb.co/TB3cbNCB",
    likes: 10,
    createdAt: new Date(),
    updatedAt: new Date(),
    author: "6887cc687882c42182cdf1f5", // paulaRunner
  },
];
