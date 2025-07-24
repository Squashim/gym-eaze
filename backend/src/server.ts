import app from "./app";
import { connectToDB } from "./config/db";

const PORT = process.env.PORT || 3000;

const startServer = async () => {
  await connectToDB();

  app.listen(PORT, () => {
    console.log(`🚀 Serwer działa na http://localhost:${PORT}`);
  });
};

startServer();
