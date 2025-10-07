import express from 'express';
import {
  addTrainingPlan,
  deleteTrainingPlan,
  downloadTrainingPlanPDF,
  getFavoriteTrainingPlans,
  getTrainingPlanById,
  getTrainingPlans,
  getTrainingPlansByUser,
  toggleTrainingPlanFavorite,
  toggleTrainingPlanLike,
  updateTrainingPlan,
} from '../controllers/trainingController';
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

router.delete('/:id', authenticate, deleteTrainingPlan);

router.patch(
  '/:id',
  authenticate,
  uploadTrainingPlanFile.fields([{ name: 'image', maxCount: 1 }]),
  handleMulterError,
  checkFileSizes,
  updateTrainingPlan,
);

router.get('/favorite', authenticate, getFavoriteTrainingPlans);

router.patch('/:id/like', authenticate, toggleTrainingPlanLike);

router.patch('/:id/favorite', authenticate, toggleTrainingPlanFavorite);

router.get('/:id/pdf', authenticate, downloadTrainingPlanPDF);

router.get('/:id', optionalAuth, getTrainingPlanById);

router.post('/search', optionalAuth, getTrainingPlans);

router.get('/user/:userId', optionalAuth, getTrainingPlansByUser);

router.get('/:id/comments', optionalAuth, (req, res) => {
  req.params.parentType = CommentParentType.post;
  req.params.parentId = req.params.id;
  return getComments(req, res);
});

export default router;
