import express, { Request, Response } from "express";

const app = express();

app.use(express.json());

// można dodać typy do req, res (takie coś 😋)
app.get("/", (req: Request, res: Response) => {
  res.send("API działa 🚀");
});

export default app;
