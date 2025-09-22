import { Request, Response } from 'express';
import { NextFunction } from 'express';

export const validateReply = (req: Request, res: Response, next: NextFunction) => {
  const { content } = req.body;
  const { commentId } = req.params;

  if (!content || typeof content !== 'string' || content.trim().length === 0) {
    return res.status(400).json({ message: 'Treść odpowiedzi jest wymagana' });
  }
  if (content.trim().length > 1000) {
    return res.status(400).json({ message: 'Odpowiedź nie może być dłuższa niż 1000 znaków' });
  }
  if (!commentId || typeof commentId !== 'string') {
    return res.status(400).json({ message: 'ID komentarza jest wymagane' });
  }
  req.body.content = content.trim();
  next();
};
