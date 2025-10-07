import { goalValues } from '../constants/goals';
import {
  deleteUserService,
  getFollowersService,
  getFollowingService,
  getUserBadgesService,
  getUserFavoritesService,
  getUserFitnessDataService,
  getUserLikesService,
  getUserPersonalDataService,
  getUserProfileDetailsService,
  getUserProfileService,
  toggleUserFollowService,
  updatePrivacySettingsService,
  updateUserFitnessDataService,
  updateUserPersonalDataService,
  updateUserProfileDetailsService,
  updateUserService,
} from '../services/userService';
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
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const profileDetails = await getUserProfileDetailsService(userId);
    return res.json(profileDetails);
  } catch (error: any) {
    console.error('Błąd w getUserProfileDetails:', error);
    return res.status(500).json({ message: error.message || 'Błąd serwera' });
  }
};

export const updateUserProfileDetails = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const { description, goal, pinnedBadges } = req.body;

    if (goal && !goalValues.includes(goal)) {
      return res.status(400).json({ message: 'Niepoprawny cel' });
    }

    const trimmedDescription = description?.trim() || '';
    if (trimmedDescription.length > 150) {
      return res.status(400).json({ message: 'Opis może mieć maksymalnie 150 znaków' });
    }

    const updated = await updateUserProfileDetailsService(
      userId,
      trimmedDescription,
      goal,
      pinnedBadges,
      req.file,
    );

    return res.json(updated);
  } catch (error: any) {
    console.error('Błąd w updateUserProfileDetails:', error);
    return res.status(500).json({ message: error.message || 'Błąd serwera' });
  }
};

export const getUserBadges = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const badges = await getUserBadgesService(userId);
    return res.json(badges);
  } catch (error: any) {
    console.error('Błąd w getUserBadges:', error);
    return res.status(500).json({ message: error.message || 'Błąd serwera' });
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
    switch (error.message) {
      case 'USERNAME_EXISTS':
        return res.status(400).json({ error: 'Nazwa użytkownika już istnieje' });
      case 'USERNAME_LENGTH':
        return res
          .status(400)
          .json({ error: 'Długość nazwy użytkownika musi wynosić od 6 do 14 znaków' });
      case 'PASSWORD_LENGTH':
        return res.status(400).json({ error: 'Długość hasła musi wynosić od 14 do 32 znaków' });
      case 'PASSWORD_UPPERCASE':
        return res
          .status(400)
          .json({ error: 'Hasło musi zawierać co najmniej jedną wielką literę' });
      case 'PASSWORD_LOWERCASE':
        return res.status(400).json({ error: 'Hasło musi zawierać co najmniej jedną małą literę' });
      case 'PASSWORD_NUMBER':
        return res.status(400).json({ error: 'Hasło musi zawierać co najmniej jedną cyfrę' });
      case 'PASSWORD_SPECIAL':
        return res
          .status(400)
          .json({ error: 'Hasło musi zawierać co najmniej jeden znak specjalny (!@#$%^&*()_+./)' });
      default:
        return res.status(500).json({ error: 'Błąd serwera' });
    }
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
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
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
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    await updateUserPersonalDataService(userId, { firstName, lastName, country });

    res.json({ message: 'Dane personalne zaktualizowane pomyślnie' });
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};

export const getUserFitnessData = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
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
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    await updateUserFitnessDataService(userId, { age, height, weight, gender });

    res.json({ message: 'Informacje fizyczne zaktualizowane pomyślnie' });
  } catch (error: any) {
    res.status(400).json({ message: error.message });
  }
};

export const updatePrivacySettings = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { privacySettings } = req.body;

    if (!privacySettings) {
      return res.status(400).json({ message: 'Ustawienia prywatności są wymagane' });
    }

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const success = await updatePrivacySettingsService(userId, privacySettings);

    if (!success) {
      return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
    }

    return res.status(200).json({
      message: 'Ustawienia prywatności zostały pomyślnie zaktualizowane',
    });
  } catch (error) {
    return res.status(500).json({
      message: 'Błąd podczas aktualizacji ustawień prywatności',
      error,
    });
  }
};

export const getFollowers = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const followers = await getFollowersService(userId);
    return res.json(followers);
  } catch (error: any) {
    console.error('Błąd w getFollowers:', error);
    return res.status(500).json({ message: error.message || 'Błąd serwera' });
  }
};

export const getFollowing = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const following = await getFollowingService(userId);
    return res.json(following);
  } catch (error: any) {
    console.error('Błąd w getFollowing:', error);
    return res.status(500).json({ message: error.message || 'Błąd serwera' });
  }
};

export const toggleUserFollow = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { id: targetUserId } = req.params;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const result = await toggleUserFollowService(userId, targetUserId);

    res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getUserProfile = async (req: AuthRequest, res: Response) => {
  try {
    const currentUserId = req.userId;
    const { id: profileUserId } = req.params;

    if (!currentUserId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const profile = await getUserProfileService(currentUserId, profileUserId);
    return res.json(profile);
  } catch (error) {
    return res.status(500).json({ message: 'Błąd serwera' });
  }
};
