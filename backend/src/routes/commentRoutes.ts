import express from 'express';
import { authenticate, optionalAuth } from '../middlewares/authMiddleware';
import {
  createComment,
  deleteComment,
  getCommentReplies,
  toggleCommentLike,
  updateComment,
} from '../controllers/commentController';
import { validateComment } from '../middlewares/commentMiddleware';

const router = express.Router();

router.post('/', authenticate, validateComment, createComment);
router.get('/:id/replies', optionalAuth, getCommentReplies);
router.patch('/:id/like', authenticate, toggleCommentLike);
router.patch('/:id', authenticate, updateComment);
router.delete('/:id', authenticate, deleteComment);

export default router;
