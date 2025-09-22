import { Response, Request } from 'express';
import { AuthRequest } from '../types/auth';
import {
  createCommentService,
  createReplyService,
  getMainCommentsWithCursor,
  getRepliesForComment,
} from '../services/commentService';
import { CreateCommentBody } from '../types/comment';

export const createComment = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { content, parentType, parentId }: CreateCommentBody = req.body;

    if (!content?.trim()) {
      return res.status(400).json({ message: 'Treść komentarza jest wymagana' });
    }

    if (!parentType || !['Post', 'Ćwiczenie', 'Trening'].includes(parentType)) {
      return res.status(400).json({ message: 'Nieprawidłowy typ rodzica komentarza' });
    }

    if (!parentId) {
      return res.status(400).json({ message: 'ID rodzica jest wymagane' });
    }

    await createCommentService(content.trim(), parentType, parentId, userId!);

    return res.status(201).json({
      message: 'Komentarz został utworzony',
    });
  } catch {
    return res.status(500).json({
      message: 'Nie udało się dodać komentarza',
    });
  }
};

export const createReply = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { content } = req.body;
    const { commentId } = req.params;

    await createReplyService(content, commentId, userId!);

    return res.status(201).json({
      message: 'Odpowiedź została dodana',
    });
  } catch {
    return res.status(500).json({
      message: 'Nie udało się dodać odpowiedzi',
    });
  }
};

export const getMainComments = async (req: Request, res: Response) => {
  try {
    const { parentType, parentId } = req.params;
    const { cursor, limit = 10 } = req.query;

    if (!parentType || !['Post', 'Ćwiczenie', 'Trening'].includes(parentType)) {
      return res.status(400).json({ message: 'Nieprawidłowy typ rodzica komentarza' });
    }

    if (!parentId) {
      return res.status(400).json({ message: 'ID rodzica jest wymagane' });
    }

    const result = await getMainCommentsWithCursor(
      parentType as 'Post' | 'Ćwiczenie' | 'Trening',
      parentId,
      cursor as string,
      parseInt(limit as string) || 10,
    );

    return res.status(200).json(result);
  } catch (error) {
    console.error('Error fetching comments:', error);
    return res.status(500).json({
      message: 'Nie udało się pobrać komentarzy',
    });
  }
};

export const getCommentReplies = async (req: Request, res: Response) => {
  try {
    const { commentId } = req.params;
    const { cursor, limit = 10 } = req.query;

    if (!commentId) {
      return res.status(400).json({ message: 'ID komentarza jest wymagane' });
    }

    const result = await getRepliesForComment(
      commentId,
      cursor as string,
      parseInt(limit as string) || 10,
    );

    return res.status(200).json(result);
  } catch (error) {
    console.error('Error fetching replies:', error);
    return res.status(500).json({
      message: 'Nie udało się pobrać odpowiedzi',
    });
  }
};
