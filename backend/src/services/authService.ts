import bcrypt from "bcryptjs";
import User from "../models/userModel";
import { OAuth2Client } from "google-auth-library";
import {
  generateAccessToken,
  generateRefreshToken,
  verifyRefreshToken,
} from "../utils/jwt";
import { JwtPayload } from "../controllers/authController";
import { IUser } from "../models/userModel";
import { HydratedDocument } from "mongoose";

export const registerUser = async (
  username: string,
  email: string,
  password: string,
) => {
  if (!username || !email || !password) {
    throw new Error("FIELDS_REQUIRED");
  }

  const checkEmail = await User.findOne({ email });
  if (checkEmail) throw new Error("EMAIL");

  const checkUsername = await User.findOne({ username });
  if (checkUsername) throw new Error("USERNAME");

  if (username.length < 6 || username.length > 16) {
    throw new Error("USERNAME_LENGTH");
  }

  if (email.length < 2 || email.length > 320) {
    throw new Error("EMAIL_LENGTH");
  }

  if (password.length < 14 || password.length > 32) {
    throw new Error("PASSWORD_LENGTH");
  }

  const passwordRegex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*()_+./])[A-Za-z\d!@#$%^&*()_+./]{14,}$/;
  if (!passwordRegex.test(password)) throw new Error("PASSWORD");

  const hashedPassword = await bcrypt.hash(password, 12);
  const user = new User({ username, email, password: hashedPassword });
  await user.save();
  return user;
};

export const loginUser = async (
  login: string,
  password: string,
): Promise<{
  user: HydratedDocument<IUser>;
  accessToken: string;
  refreshToken: string;
}> => {
  const user = await User.findOne({
    $or: [{ email: login }, { username: login }],
  });

  if (!user || !(await bcrypt.compare(password, user.password))) {
    throw new Error("INVALID_CREDENTIALS");
  }

  const accessToken = generateAccessToken(user._id.toString());
  const refreshToken = generateRefreshToken(user._id.toString());

  user.refreshToken = refreshToken;
  await user.save();

  return { user, accessToken, refreshToken };
};

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

export const googleLogin = async (token: string) => {
  const ticket = await client.verifyIdToken({
    idToken: token,
    audience: process.env.GOOGLE_CLIENT_ID,
  });

  const payload = ticket.getPayload();
  if (!payload) throw new Error("INVALID_GOOGLE_TOKEN");

  const { sub: googleId, email, name } = payload;

  let user = await User.findOne({ email });

  if (!user) {
    user = new User({
      username: name || email?.split("@")[0],
      email,
      googleId,
    });
    await user.save();
  } else if (!user.googleId) {
    user.googleId = googleId;
    await user.save();
  }

  const accessToken = generateAccessToken(user._id.toString());
  const refreshToken = generateRefreshToken(user._id.toString());

  user.refreshToken = refreshToken;
  await user.save();

  return { user, accessToken, refreshToken };
};

export const refreshAccessToken = async (token: string) => {
  const payload = verifyRefreshToken(token) as JwtPayload;
  const user = await User.findById(payload.userId);
  if (!user || user.refreshToken !== token) throw new Error("INVALID_REFRESH");

  return generateAccessToken(user._id.toString());
};

export const logoutUser = async (token: string) => {
  const payload = verifyRefreshToken(token) as JwtPayload;
  const user = await User.findById(payload.userId);
  if (user) {
    user.refreshToken = undefined;
    await user.save();
  }
};
