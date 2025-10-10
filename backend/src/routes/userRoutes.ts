import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {
  deleteUser,
  getFollowers,
  getFollowing,
  getPrivacySettings,
  getUserBadges,
  getUserFavorites,
  getUserFitnessData,
  getUserLikes,
  getUserPersonalData,
  getUserProfile,
  getUserProfileDetails,
  toggleUserFollow,
  updatePrivacySettings,
  updateUser,
  updateUserFitnessData,
  updateUserPersonalData,
  updateUserProfileDetails,
} from '../controllers/userController';
import {
  checkProfilePictureSize,
  handleProfileMulterError,
  uploadProfileImage,
} from '../middlewares/userMiddleware';

const router = express.Router();

router.get('/likes', authenticate, getUserLikes);
router.get('/favorites', authenticate, getUserFavorites);

router.get('/profile/profile-details', authenticate, getUserProfileDetails);

router.patch(
  '/profile/profile-details',
  authenticate,
  uploadProfileImage,
  handleProfileMulterError,
  checkProfilePictureSize,
  updateUserProfileDetails,
);

router.get('/badges', authenticate, getUserBadges);

router.patch('/login-details', authenticate, updateUser);

router.delete('/', authenticate, deleteUser);

router.get('/profile/personal-details', authenticate, getUserPersonalData);

router.patch('/profile/personal-details', authenticate, updateUserPersonalData);

router.get('/profile/fitness-details', authenticate, getUserFitnessData);
router.patch('/profile/fitness-details', authenticate, updateUserFitnessData);

router.get('/privacy-settings', authenticate, getPrivacySettings);
router.patch('/privacy-settings', authenticate, updatePrivacySettings);

router.get('/followers', authenticate, getFollowers);

router.get('/following', authenticate, getFollowing);

router.patch('/:id/follow', authenticate, toggleUserFollow);

router.get('/:id/profile', authenticate, getUserProfile);

export default router;
