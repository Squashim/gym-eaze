import { Request, Response, NextFunction } from 'express';
import { verifyAccessToken } from '../utils/jwt';
import { JwtPayload } from 'jsonwebtoken';
import jwt from 'jsonwebtoken';

interface AuthRequest extends Request {
  userId?: string;
}

export const authenticate = (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const token = req.cookies?.accessToken || req.headers.authorization?.split(' ')[1];

    if (!token) {
      return res.status(401).json({ message: 'Brak tokenu autoryzacyjnego' });
    }

    const payload = verifyAccessToken(token) as JwtPayload;
    req.userId = payload.userId;
    next();
  } catch (err) {
    if (err instanceof jwt.TokenExpiredError) {
      return res.status(401).json({ message: 'Token wygasł' });
    }
    return res.status(403).json({ message: 'Nieprawidłowy token' });
  }
};

export const nonAuthenticate = (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const token = req.cookies.accessToken || req.headers.authorization?.split(' ')[1];
    if (!token) return next();
    const payload = verifyAccessToken(token) as JwtPayload;
    if (payload.userId) {
      return res.status(403).json({ message: 'Użytkownik jest już zalogowany' });
    }
  } catch {
    return next();
  }

  next();
};

export const optionalAuth = (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const token = req.cookies?.accessToken || req.headers.authorization?.split(' ')[1];
    if (token) {
      const payload = verifyAccessToken(token) as JwtPayload;
      req.userId = payload.userId;
    }
  } catch {
    req.userId = undefined;
  }
  next();
};
