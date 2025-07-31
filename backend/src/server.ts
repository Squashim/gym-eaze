import dotenv from "dotenv";
dotenv.config();

import app from "./app";
import { connectToDB } from "./config/db";

const PORT = process.env.PORT;

const startServer = async () => {
  await connectToDB();

  app.listen(PORT, () => {
    console.log(`🚀 Serwer działa na http://localhost:${PORT}`);
  });
};

startServer();
