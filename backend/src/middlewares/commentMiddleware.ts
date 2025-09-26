import { Request, Response } from 'express';
import { NextFunction } from 'express';
import { parseDescription } from '../utils/parsers';
import { commentParentTypeValues } from '../constants/commentType';

export const validateComment = (req: Request, res: Response, next: NextFunction) => {
  const { content, parentType, parentId } = req.body;

  if (!content || typeof content !== 'string' || content.trim().length === 0) {
    return res.status(400).json({ message: 'Treść komentarza jest wymagana' });
  }

  if (parseDescription(content.trim()).length > 1000) {
    return res.status(400).json({ message: 'Komentarz nie może być dłuższy niż 1000 znaków' });
  }

  if (!parentId || typeof parentId !== 'string') {
    return res.status(400).json({ message: 'Identyfikator rodzica jest wymagany' });
  }

  if (!parentType || !commentParentTypeValues.includes(parentType)) {
    return res.status(400).json({ message: 'Nieprawidłowy typ rodzica komentarza' });
  }

  req.body.content = content.trim();
  next();
};
