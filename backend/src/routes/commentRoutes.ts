import express from 'express';
import { authenticate, optionalAuth } from '../middlewares/authMiddleware';
import { createComment, getCommentReplies } from '../controllers/commentController';
import { validateComment } from '../middlewares/commentMiddleware';

const router = express.Router();

router.post('/', authenticate, validateComment, createComment);
router.get('/:id/replies', optionalAuth, getCommentReplies);
router.patch('/:id');
router.delete('/:id');

export default router;
