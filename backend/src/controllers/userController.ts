import { goalValues } from '../constants/goals';
import {
  deleteUserService,
  getFollowersService,
  getFollowingService,
  getPrivacySettingsService,
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
import { parseDescription } from '../utils/parsers';

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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const updateUserProfileDetails = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const { description, goal } = req.body;
    let { pinnedBadges } = req.body;

    if (goal && !goalValues.includes(goal)) {
      return res.status(400).json({ message: 'Niepoprawny cel' });
    }

    const trimmedDescription = description?.trim() || '';
    if (parseDescription(trimmedDescription).length > 150) {
      return res.status(400).json({ message: 'Opis może mieć maksymalnie 150 znaków' });
    }

    if (pinnedBadges && typeof pinnedBadges === 'string') {
      try {
        pinnedBadges = JSON.parse(pinnedBadges);
      } catch {
        return res.status(400).json({ message: 'Nieprawidłowy format odznak' });
      }
    }

    const updated = await updateUserProfileDetailsService(
      userId,
      trimmedDescription,
      goal,
      pinnedBadges,
      req.file,
    );

    return res.json(updated);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (!(error instanceof Error)) {
      return res.status(500).json({ message: 'Błąd serwera' });
    }

    switch (error.message) {
      case 'USERNAME_EXISTS':
        return res.status(400).json({ message: 'Nazwa użytkownika już istnieje' });
      case 'USERNAME_LENGTH':
        return res
          .status(400)
          .json({ message: 'Długość nazwy użytkownika musi wynosić od 6 do 14 znaków' });
      case 'PASSWORD_LENGTH':
        return res.status(400).json({ message: 'Długość hasła musi wynosić od 14 do 32 znaków' });
      case 'PASSWORD_UPPERCASE':
        return res
          .status(400)
          .json({ message: 'Hasło musi zawierać co najmniej jedną wielką literę' });
      case 'PASSWORD_LOWERCASE':
        return res
          .status(400)
          .json({ message: 'Hasło musi zawierać co najmniej jedną małą literę' });
      case 'PASSWORD_NUMBER':
        return res.status(400).json({ message: 'Hasło musi zawierać co najmniej jedną cyfrę' });
      case 'PASSWORD_SPECIAL':
        return res.status(400).json({
          message: 'Hasło musi zawierać co najmniej jeden znak specjalny (!@#$%^&*()_+./)',
        });
      case 'SAME_PASSWORD':
        return res.status(400).json({ message: 'Nowe hasło musi być inne niż stare hasło' });
      default:
        return res.status(500).json({ message: 'Błąd serwera' });
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

    res.clearCookie('refreshToken');
    res.clearCookie('accessToken');
    res.status(200).json({ message: 'Konto zostało usunięte pomyślnie' });
  } catch {
    res.status(500).json({ message: 'Błąd serwera podczas usuwania konta' });
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getPrivacySettings = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });
    }

    const privacySettings = await getPrivacySettingsService(userId);

    if (!privacySettings) {
      return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
    }

    return res.status(200).json({
      message: 'Ustawienia prywatności pobrane pomyślnie',
      data: privacySettings,
    });
  } catch (error) {
    return res.status(500).json({
      message: 'Błąd podczas pobierania ustawień prywatności',
      error,
    });
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
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
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
