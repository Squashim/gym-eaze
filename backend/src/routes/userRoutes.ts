import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import { getUserFavorites, getUserLikes } from '../controllers/userController';

const router = express.Router();

router.get("/likes", authenticate, getUserLikes);
router.get("/favorites", authenticate, getUserFavorites);

export default router;
