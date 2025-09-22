import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {
  createComment,
  createReply,
  getCommentReplies,
  getMainComments,
} from '../controllers/commentController';
import { validateReply } from '../middlewares/commentMiddleware';

const router = express.Router();

router.post('/', authenticate, createComment);

router.get('/replies/:commentId', getCommentReplies);

router.post('/:commentId/reply', authenticate, validateReply, createReply);

router.get('/:parentType/:parentId', getMainComments);

export default router;
