import { Response, Request } from 'express';
import { AuthRequest } from '../types/auth';
import {
  createCommentService,
  getCommentsWithCursor,
  getRepliesForComment,
} from '../services/commentService';
import { CreateCommentBody } from '../types/comment';
import {
  CommentParentType,
  MainCommentParentType,
  MainCommentParentTypes,
  mainCommentParentTypes,
} from '../constants/commentType';

export const createComment = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { content, parentType, parentId }: CreateCommentBody = req.body;

    await createCommentService(content, parentType, parentId, userId!);

    return res.status(201).json({
      message:
        parentType === CommentParentType.comment
          ? 'Odpowiedź została dodana'
          : 'Komentarz został dodany',
    });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas dodawania komentarza/odpowiedzi',
    });
  }
};

export const getComments = async (req: Request, res: Response) => {
  try {
    const { parentType, parentId } = req.params;
    const { cursor, limit = 10 } = req.query;

    if (!parentType || !mainCommentParentTypes.includes(parentType as MainCommentParentType)) {
      return res.status(400).json({ message: 'Nieprawidłowy typ rodzica komentarza' });
    }

    if (!parentId) {
      return res.status(400).json({ message: 'Identyfikator rodzica jest wymagany' });
    }

    const result = await getCommentsWithCursor(
      parentType as MainCommentParentTypes,
      parentId,
      cursor as string,
      parseInt(limit as string) || 10,
    );

    return res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Nie udało się pobrać komentarzy',
    });
  }
};

export const getCommentReplies = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const { cursor, limit = 10 } = req.query;

    if (!id) {
      return res.status(400).json({ message: 'Identyfikator komentarza jest wymagany' });
    }

    const result = await getRepliesForComment(
      id,
      cursor as string,
      parseInt(limit as string) || 10,
    );

    return res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Nie udało się pobrać odpowiedzi',
    });
  }
};
