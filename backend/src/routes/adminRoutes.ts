import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import { blockUser, deleteContent } from '../controllers/adminController';

const router = express.Router();

router.delete('/content/:itemId/:itemType', authenticate, deleteContent);
router.patch('/users/:id/block', authenticate, blockUser);

export default router;
