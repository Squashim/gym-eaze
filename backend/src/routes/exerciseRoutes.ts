import { Router } from 'express';
import {
  addExercise,
  getExerciseById,
  getExercises,
  getExercisesByIds,
  getExercisesByUser,
  getFavoriteExercises,
  toggleExerciseFavorite,
  toggleExerciseLike,
} from '../controllers/exerciseController';
import {
  uploadExerciseFiles,
  handleMulterError,
  checkFileSizes,
} from '../middlewares/exerciseMiddleware';
import { authenticate, optionalAuth } from '../middlewares/authMiddleware';
import { deleteExercise, updateExercise } from '../controllers/exerciseController';
import { CommentParentType } from '../constants/commentType';
import { getComments } from '../controllers/commentController';

const router = Router();

router.post(
  '/',
  authenticate,
  uploadExerciseFiles.fields([
    { name: 'image', maxCount: 1 },
    { name: 'video', maxCount: 1 },
  ]),
  handleMulterError,
  checkFileSizes,
  addExercise,
);

router.delete('/:id', authenticate, deleteExercise);

router.patch(
  '/:id',
  authenticate,
  uploadExerciseFiles.fields([
    { name: 'image', maxCount: 1 },
    { name: 'video', maxCount: 1 },
  ]),
  handleMulterError,
  checkFileSizes,
  updateExercise,
);

router.get('/favorite', authenticate, getFavoriteExercises);

router.patch('/:id/like', authenticate, toggleExerciseLike);

router.patch('/:id/favorite', authenticate, toggleExerciseFavorite);

router.get('/:id', optionalAuth, getExerciseById);

router.post('/batch', optionalAuth, getExercisesByIds);

router.post('/search', optionalAuth, getExercises);

router.get('/user/:userId', optionalAuth, getExercisesByUser);

router.get('/:id/comments', optionalAuth, (req, res) => {
  req.params.parentType = CommentParentType.exercise;
  req.params.parentId = req.params.id;
  return getComments(req, res);
});

export default router;
