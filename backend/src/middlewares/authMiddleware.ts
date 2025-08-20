import { Request, Response, NextFunction } from 'express';
import { verifyAccessToken } from '../utils/jwt';
import { JwtPayload } from 'jsonwebtoken';

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
    req.userId = payload.userId as string;
    next();
  } catch {
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
