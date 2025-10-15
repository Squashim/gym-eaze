import express from 'express';
import { authenticate, optionalAuth } from '../middlewares/authMiddleware';
import {
  uploadExerciseFiles,
  handleMulterError,
  checkFileSizes,
} from '../middlewares/exerciseMiddleware';
import {
  addPost,
  deletePost,
  getPosts,
  getPostsByUser,
  togglePostLike,
  updatePost,
} from '../controllers/postController';
import { CommentParentType } from '../constants/commentType';
import { getComments } from '../controllers/commentController';

const router = express.Router();

router.post(
  '/',
  authenticate,
  uploadExerciseFiles.fields([
    { name: 'image', maxCount: 1 },
    { name: 'video', maxCount: 1 },
  ]),
  handleMulterError,
  checkFileSizes,
  addPost,
);

router.patch(
  '/:id',
  authenticate,
  uploadExerciseFiles.fields([
    { name: 'image', maxCount: 1 },
    { name: 'video', maxCount: 1 },
  ]),
  handleMulterError,
  checkFileSizes,
  updatePost,
);

router.delete('/:id', authenticate, deletePost);

router.patch('/:id/like', authenticate, togglePostLike);

router.get('/', optionalAuth, getPosts);

router.get('/user/:userId', optionalAuth, getPostsByUser);

router.get('/:id/comments', optionalAuth, (req, res) => {
  req.params.parentType = CommentParentType.post;
  req.params.parentId = req.params.id;
  return getComments(req, res);
});

export default router;
