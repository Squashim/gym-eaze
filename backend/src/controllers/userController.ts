import { getUserFavoritesService, getUserLikesService } from "../services/userService";
import { AuthRequest } from "../types/auth";
import { Response } from "express";

export const getUserLikes = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const likes = await getUserLikesService(userId);
    res.status(200).json(likes);
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};

export const getUserFavorites = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const favorites = await getUserFavoritesService(userId);
    res.status(200).json(favorites);
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};