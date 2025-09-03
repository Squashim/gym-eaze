import { Request, Response } from 'express';
import * as hashtagService from '../services/HashtagService';

export const getAllHashtags = async (req: Request, res: Response): Promise<void> => {
  try {
    const hashtags = await hashtagService.getAllHashtags();
    res.json(hashtags);
  } catch (error) {
    console.error('Error fetching hashtags:', error);
    res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const getPopularHashtags = async (req: Request, res: Response): Promise<void> => {
  try {
    const limit = parseInt(req.query.limit as string) || 20;
    const hashtags = await hashtagService.getPopularHashtags(limit);
    res.json(hashtags);
  } catch (error) {
    console.error('Error fetching popular hashtags:', error);
    res.status(500).json({ message: 'Błąd serwera' });
  }
};