import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {getUserBadges, getUserFavorites, getUserLikes, getUserProfileDetails, updateUserProfileDetails} from '../controllers/userController';
import { checkProfilePictureSize, handleProfileMulterError, uploadProfileImage} from '../middlewares/userMiddleware';

const router = express.Router();

router.get("/likes", authenticate, getUserLikes);
router.get("/favorites", authenticate, getUserFavorites);

router.get("/profile/profileDetails", authenticate, getUserProfileDetails);

router.patch(
  "/profile/profileDetails",
  authenticate,
  uploadProfileImage,
  handleProfileMulterError,
  checkProfilePictureSize,
  updateUserProfileDetails
);

router.get("/badges", authenticate, getUserBadges);

export default router;
