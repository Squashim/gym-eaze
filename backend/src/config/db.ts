import mongoose from "mongoose";
import dotenv from "dotenv";

dotenv.config();

const MONGODB_URI = process.env.MONGODB_URI;

if (!MONGODB_URI) {
  throw new Error("Brakuje zmiennej środowiskowej MONGODB_URI");
}

export const connectToDB = async () => {
  try {
    await mongoose.connect(MONGODB_URI);
    console.log("✅ Połączono z MongoDB");
  } catch (err) {
    console.error("Błąd połączenia z MongoDB:", err);
    process.exit(1);
  }
};
