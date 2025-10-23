import { Response } from 'express';
import { AuthRequest } from '../types/auth';
import {
  deleteNotificationService,
  getNotificationsService,
  sendNotificationService,
} from '../services/notificationService';

export const sendNotification = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { message, from, to, type } = req.body;

    await sendNotificationService(message, to, type, from, userId);

    return res.status(201).json({
      message: 'Powiadomienie zostało wysłane pomyślnie',
    });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas wysyłania powiadomienia',
    });
  }
};

export const deleteNotification = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    const message = await deleteNotificationService(userId!);

    return res.status(200).json({
      message: message,
    });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({
      message: 'Wystąpił niezidetyfikowany błąd podczas usuwania powiadomień',
    });
  }
};

export const getNotifications = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { cursor, limit = 10 } = req.query;

    const notifications = await getNotificationsService(Number(limit), userId!, cursor as string);

    return res.json(notifications);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }

    return res.status(500).json({
      message: 'Błąd serwera',
    });
  }
};
