import { Request, Response, NextFunction } from 'express';
import { verifyAccessToken } from '../utils/jwt';
import { JwtPayload } from 'jsonwebtoken';

interface AuthRequest extends Request {
  userId?: string;
}

export const authenticate = (req: AuthRequest, res: Response, next: NextFunction) => {
  try {
    const token = req.cookies.accessToken;

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
