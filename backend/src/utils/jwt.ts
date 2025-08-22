import jwt, { JwtPayload } from 'jsonwebtoken';
import type { SignOptions } from 'jsonwebtoken';

const ACCESS_SECRET = process.env.ACCESS_SECRET as string;
const REFRESH_SECRET = process.env.REFRESH_SECRET as string;

if (!ACCESS_SECRET || !REFRESH_SECRET) {
  throw new Error('Brak zmiennych w env ACCESS_SECRET lub REFRESH_SECRET');
}

export const generateAccessToken = (userId: string) => {
  return jwt.sign({ userId }, ACCESS_SECRET, { expiresIn: '15m' });
};

export const generateRefreshToken = (userId: string, expiresIn: string | number) => {
  return jwt.sign({ userId }, REFRESH_SECRET as string, {
    expiresIn: expiresIn as SignOptions['expiresIn'],
  });
};

export const verifyAccessToken = (token: string) => {
  return jwt.verify(token, ACCESS_SECRET) as JwtPayload;
};

export const verifyRefreshToken = (token: string) => {
  return jwt.verify(token, REFRESH_SECRET) as JwtPayload;
};
