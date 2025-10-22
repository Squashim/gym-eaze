import { Response } from 'express';
import { AuthRequest } from '../types/auth';
import { blockUserService, deleteContentService } from '../services/adminService';

export const blockUser = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id } = req.params;

    await blockUserService(id, userId!);

    return res.status(201).json({
      message: 'Użytkownik został zablokowany',
    });
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND' || error.message === 'USER_TO_BLOCK_NOT_FOUND') {
        return res.status(404).json({ message: error.message });
      }

      if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: error.message });
      }

      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas blokowania użytkownika',
    });
  }
};

export const deleteContent = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { itemType, itemId } = req.params;

    const message = await deleteContentService(userId!, itemType, itemId);

    return res.status(200).json({
      message: message,
    });
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND') {
        return res.status(404).json({ message: error.message });
      }

      if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: error.message });
      }

      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas usuwania treści',
    });
  }
};
