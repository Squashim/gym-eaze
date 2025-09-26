import express from 'express';
import { addTrainingPlan } from '../controllers/trainingController';
import { authenticate, optionalAuth } from '../middlewares/authMiddleware';
import {
  checkFileSizes,
  handleMulterError,
  uploadTrainingPlanFile,
} from '../middlewares/trainingPlanMiddleware';
import { CommentParentType } from '../constants/commentType';
import { getComments } from '../controllers/commentController';

const router = express.Router();

router.post(
  '/',
  authenticate,
  uploadTrainingPlanFile.fields([{ name: 'image', maxCount: 1 }]),
  handleMulterError,
  checkFileSizes,
  addTrainingPlan,
);

router.get('/:id/comments', optionalAuth, (req, res) => {
  req.params.parentType = CommentParentType.post;
  req.params.parentId = req.params.id;
  return getComments(req, res);
});

export default router;
