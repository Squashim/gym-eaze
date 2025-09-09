import { goalValues } from '../constants/goals';
import {deleteUserService, getUserBadgesService, getUserFavoritesService, getUserFitnessDataService, getUserLikesService, getUserPersonalDataService, getUserProfileDetailsService, updateUserFitnessDataService, updateUserPersonalDataService, updateUserProfileDetailsService, updateUserService} from '../services/userService';
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

export const updateUser = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId; 
    const { username, password } = req.body;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const updatedUser = await updateUserService(userId, { username, password });

    res.status(200).json({ message: 'Użytkownik zaktualizowany pomyślnie', user: updatedUser });
  } catch (error: any) {
    if (error.message === 'USERNAME_EXISTS') {
      return res.status(400).json({ error: 'Nazwa użytkownika już istnieje' });
    }
    if (error.message === 'USERNAME_LENGTH') {
      return res.status(400).json({ error: 'Długość nazwy użytkownika musi wynosić od 6 do 16 znaków' });
    }
    if (error.message === 'PASSWORD_LENGTH') {
      return res.status(400).json({ error: 'Długość hasła musi wynosić od 14 do 32 znaków' });
    }
    if (error.message === 'INVALID_PASSWORD') {
      return res.status(400).json({ error: 'Hasło nie spełnia wymagań dotyczących złożoności' });
    }
    res.status(500).json({ error: 'Błąd serwera' });
  }
};

export const deleteUser = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    await deleteUserService(userId);

    res.status(200).json({ message: 'Konto zostało usunięte pomyślnie' });
  } catch (error) {
    res.status(500).json({ error: 'Błąd serwera podczas usuwania konta' });
  }
};

export const getUserPersonalData = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }
    const personalData = await getUserPersonalDataService(userId);
    res.json(personalData);
  } catch (error: any) {
    res.status(500).json({ message: error.message });
  }
};

export const updateUserPersonalData = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId; 
    const { firstName, lastName, country } = req.body;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    await updateUserPersonalDataService(userId, { firstName, lastName, country });

    res.json({ message: "Dane osobowe zaktualizowane pomyślnie" });
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};

export const getUserFitnessData = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    const fitnessData = await getUserFitnessDataService(userId);
    res.json(fitnessData);
  } catch (error: any) {
    res.status(500).json({ message: error.message });
  }
};

export const updateUserFitnessData = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { age, height, weight, gender } = req.body;

    if (!userId) {
      return res.status(401).json({ message: "Brak autoryzacji użytkownika" });
    }

    await updateUserFitnessDataService(userId, { age, height, weight, gender });

    res.json({ message: "Dane fitness zaktualizowane pomyślnie" });
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};

