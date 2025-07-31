import express, { Request, Response } from "express";
import cors from "cors";
import cookieParser from "cookie-parser";
import authRoutes from "../src/routes/authRoutes";

const app = express();

app.use(cookieParser());
app.use(
  cors({
    origin: "http://localhost:3000", // frontend origin
    credentials: true, // to allow cookies
  }),
);
app.use(express.json());
app.use("/api/auth", authRoutes);

app.get("/", (req: Request, res: Response) => {
  res.send("API działa 🚀");
});

export default app;
