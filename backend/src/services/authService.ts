import bcrypt from 'bcryptjs';
import User from '../models/userModel';
import { generateAccessToken, generateRefreshToken, verifyRefreshToken } from '../utils/jwt';
import { JwtPayload } from 'jsonwebtoken';
import { ICurrentUser } from '../types/auth';
import DeviceToken from '../models/deviceTokenModel';

export const registerUser = async (username: string, email: string, password: string) => {
  if (!username || !email || !password) {
    throw new Error('REQUIRED_FIELDS');
  }

  const checkEmail = await User.findOne({ email });
  if (checkEmail) throw new Error('EMAIL');

  const checkUsername = await User.findOne({ username });
  if (checkUsername) throw new Error('USERNAME');

  if (username.length < 6 || username.length > 16) {
    throw new Error('USERNAME_LENGTH');
  }

  if (email.length < 2 || email.length > 320) {
    throw new Error('EMAIL_LENGTH');
  }

  if (password.length < 14 || password.length > 32) {
    throw new Error('PASSWORD_LENGTH');
  }

  const passwordRegex =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*()_+./])[A-Za-z\d!@#$%^&*()_+./]{14,}$/;
  if (!passwordRegex.test(password)) throw new Error('INVALID_PASSWORD');

  const hashedPassword = await bcrypt.hash(password, 12);
  const user = new User({ username, email, password: hashedPassword });
  await user.save();
};

export const loginUser = async (
  login: string,
  password: string,
  deviceInfo: string,
  rememberMe: boolean,
) => {
  const user = await User.findOne({
    $or: [{ email: login }, { username: login }],
  });

  if (!user || !(await bcrypt.compare(password, user.password))) {
    throw new Error('INVALID_CREDENTIALS');
  }

  const isFirstLogin = !user.lastLoginAt;

  user.lastLoginAt = new Date();
  await user.save();

  const accessToken = generateAccessToken(user._id.toString());
  const refreshTokenExpiry = rememberMe ? '30d' : '7d';
  const refreshToken = generateRefreshToken(user._id.toString(), refreshTokenExpiry);

  const deviceCount = await DeviceToken.countDocuments({ userId: user._id });

  if (deviceCount >= 5) {
    await DeviceToken.findOneAndDelete({ userId: user._id }, { sort: { createdAt: 1 } });
  }

  const device = new DeviceToken({
    userId: user._id,
    refreshToken,
    deviceInfo,
  });

  await device.save();

  return { accessToken, refreshToken, isFirstLogin };
};

export const googleLogin = async (token: string, deviceInfo: string, rememberMe: boolean) => {
  const response = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error('INVALID_GOOGLE_TOKEN');
  }

  const { sub: googleId, email, name, picture } = await response.json();

  let user = await User.findOne({ email });

  if (!user) {
    user = new User({
      username: name || email?.split('@')[0],
      email,
      googleId,
      profileDetails: {
        profilePicture: picture,
      },
    });
    await user.save();
  } else if (!user.googleId) {
    user.googleId = googleId;
    await user.save();
  }

  const accessToken = generateAccessToken(user._id.toString());
  const refreshTokenExpiry = rememberMe ? '30d' : '7d';
  const refreshToken = generateRefreshToken(user._id.toString(), refreshTokenExpiry);
  const device = new DeviceToken({
    userId: user._id,
    refreshToken,
    deviceInfo: deviceInfo,
  });

  await device.save();

  return { accessToken, refreshToken };
};

export const getCurrentUser = async (userId: string): Promise<ICurrentUser> => {
  const user = await User.findById(userId)
    .select('username email role profileDetails.profilePicture createdAt updatedAt')
    .populate({
      path: 'notifications',
      select: 'type message updatedAt createdAt',
    })
    .lean<ICurrentUser>();

  if (!user) {
    throw new Error('Użytkownik nie istnieje');
  }

  return user;
};

export const refreshAccessToken = async (token: string) => {
  if (!token) {
    throw new Error('Brak tokena odświeżającego');
  }
  const payload = verifyRefreshToken(token) as JwtPayload;

  if (!payload || typeof payload.userId !== 'string') {
    throw new Error('Niepoprawny token odświeżający');
  }

  const user = await User.findById(payload.userId);
  const deviceToken = await DeviceToken.findOne({ userId: payload.userId, refreshToken: token });

  if (!user || !deviceToken) {
    throw new Error('Niepoprawny token odświeżający');
  }

  return generateAccessToken(user._id.toString());
};

export const logoutUser = async (token: string) => {
  const payload = verifyRefreshToken(token) as JwtPayload;
  const deviceToken = await DeviceToken.findOne({ userId: payload.userId, refreshToken: token });

  const user = await User.findById(payload.userId);

  if (!user) {
    throw new Error('Nie ma takiego użytkownika');
  }

  if (!deviceToken) {
    throw new Error('Nie ma takiego tokenu');
  }

  await deviceToken.deleteOne();
};
