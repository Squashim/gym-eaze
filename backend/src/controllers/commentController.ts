import { Response, Request } from 'express';
import { AuthRequest } from '../types/auth';
import {
  createCommentService,
  deleteCommentService,
  getCommentsWithCursor,
  getRepliesForComment,
  toggleCommentLikeService,
  updateCommentService,
} from '../services/commentService';
import { CreateCommentBody } from '../types/comment';
import {
  CommentParentType,
  MainCommentParentType,
  MainCommentParentTypes,
  mainCommentParentTypes,
} from '../constants/commentType';
import { parseDescription } from '../utils/parsers';

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

export const updateComment = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;
    const { content } = req.body;

    if (!content || content.trim() === '') {
      return res.status(400).json({ message: 'Treść komentarza jest wymagana' });
    }

    if (parseDescription(content.trim()).length > 1000) {
      return res.status(400).json({ message: 'Komentarz nie może być dłuższy niż 1000 znaków' });
    }

    const message = await updateCommentService(id, userId!, content);

    return res.status(200).json({
      message: message,
    });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas edytowania komentarza/odpowiedzi',
    });
  }
};

export const deleteComment = async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const message = await deleteCommentService(id, userId!);

    return res.status(200).json({
      message: message,
    });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas usuwania komentarza/odpowiedzi',
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

export const toggleCommentLike = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleCommentLikeService(userId, id);

    res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
