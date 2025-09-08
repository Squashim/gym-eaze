import { goalValues } from '../constants/goals';
import {getUserBadgesService, getUserFavoritesService, getUserLikesService, getUserProfileDetailsService, updateUserProfileDetailsService} from '../services/userService';
import { AuthRequest } from '../types/auth';
import { Response } from 'express';

export const getUserLikes = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const likes = await getUserLikesService(userId);
    res.status(200).json(likes);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getUserFavorites = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const favorites = await getUserFavoritesService(userId);
    res.status(200).json(favorites);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getUserProfileDetails = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const profileDetails = await getUserProfileDetailsService(userId);
    return res.json(profileDetails);
  } catch (error: any) {
    console.error("Błąd w getUserProfileDetails:", error);
    return res.status(500).json({ message: error.message || "Błąd serwera" });
  }
};

export const updateUserProfileDetails = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const { description, goal, pinnedBadges } = req.body;

    if (goal && !goalValues.includes(goal)) {
      return res.status(400).json({ message: "Niepoprawny cel" });
    }

    const trimmedDescription = description?.trim() || "";

    const updated = await updateUserProfileDetailsService(
      userId,
      trimmedDescription,
      goal,
      pinnedBadges,
      req.file
    );

    return res.json(updated);
  } catch (error: any) {
    console.error("Błąd w updateUserProfileDetails:", error);
    return res.status(500).json({ message: error.message || "Błąd serwera" });
  }
};

export const getUserBadges = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const badges = await getUserBadgesService(userId);
    return res.json(badges);
  } catch (error: any) {
    console.error("Błąd w getUserBadges:", error);
    return res.status(500).json({ message: error.message || "Błąd serwera" });
  }
};

