import { Request, Response } from "express";
import bcrypt from "bcryptjs";
import {
  generateAccessToken,
  generateRefreshToken,
  verifyRefreshToken,
} from "../utils/jwt";
import User from "../models/userModel";

export interface JwtPayload {
  userId: string;
  username?: string;
  email?: string;
}

export const register = async (req: Request, res: Response) => {
  const { username, email, password } = req.body;

  const existingEmail = await User.findOne({ email });
  if (existingEmail)
    return res
      .status(400)
      .json({ message: "Użytkownik o takim emailu już istnieje" });

  const existingUsername = await User.findOne({ username });
  if (existingUsername)
    return res
      .status(400)
      .json({ message: "Użytkownik o takim username już istnieje" });

  const passwordRegex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{14,}$/;
  if (!passwordRegex.test(password)) {
    return res.status(400).json({
      message:
        "Hasło musi mieć minimum 14 znaków posiadać co najmniej jedną wielką literę jedna mala litere jedną cyfrę i jeden znak specjalny (!@#$%^&*()_+)",
    });
  }

  const hashed = await bcrypt.hash(password, 12);

  const user = new User({ username, email, password: hashed });
  await user.save();

  res.status(201).json({ message: "Rejestracja udana" });
};

export const login = async (req: Request, res: Response) => {
  const { login, password } = req.body;
  if (!login || !password) {
    return res
      .status(400)
      .json({ message: "Username/email i hasło są wymagane" });
  }
  const user = await User.findOne({
    $or: [{ email: login }, { username: login }],
  });

  if (!user || !(await bcrypt.compare(password, user.password))) {
    return res.status(401).json({ message: "Nieprawidłowe dane" });
  }

  const accessToken = generateAccessToken(user._id.toString());
  const refreshToken = generateRefreshToken(user._id.toString());

  user.refreshToken = refreshToken;
  await user.save();

  res.cookie("refreshToken", refreshToken, {
    httpOnly: true,
    secure: true,
    sameSite: "strict",
    maxAge: 7 * 24 * 60 * 60 * 1000,
  });

  res.status(200).json({
    message: "Logowanie udane",
    accessToken,
  });
};

export const refresh = async (req: Request, res: Response) => {
  const token = req.cookies.refreshToken || req.body.token;
  if (!token) return res.sendStatus(401);

  try {
    const payload = verifyRefreshToken(token) as JwtPayload;

    const user = await User.findById(payload.userId);
    if (!user || user.refreshToken !== token) return res.sendStatus(403);

    const newAccessToken = generateAccessToken(user._id.toString());
    res.json({ accessToken: newAccessToken });
  } catch {
    res.sendStatus(403);
  }
};

export const logout = async (req: Request, res: Response) => {
  const token = req.cookies.refreshToken || req.body.token;
  if (!token) return res.sendStatus(400);

  try {
    const payload = verifyRefreshToken(token) as JwtPayload;
    const user = await User.findById(payload.userId);
    if (user) {
      user.refreshToken = undefined;
      await user.save();
    }

    res.clearCookie("refreshToken");
    res.json({ message: "Wylogowano" });
  } catch {
    res.sendStatus(403);
  }
};
