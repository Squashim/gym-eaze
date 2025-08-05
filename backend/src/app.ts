import express, { Request, Response } from "express";
import cors from "cors";
import cookieParser from "cookie-parser";
import authRoutes from "../src/routes/authRoutes";
import { swaggerUi, swaggerSpec } from "../src/config/swagger";

const app = express();

app.use(cookieParser());
app.use(
  cors({
    origin: "http://localhost:3000",
    credentials: true,
  }),
);
app.use(express.json());
app.use("/api/auth", authRoutes);

app.get("/", (req: Request, res: Response) => {
  res.send("API działa 🚀");
});

app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));

export default app;
