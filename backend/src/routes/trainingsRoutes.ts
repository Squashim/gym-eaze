import express from 'express';
import { addTrainingPlan } from '../controllers/trainingController';
import { authenticate } from '../middlewares/authMiddleware';
import {
  checkFileSizes,
  handleMulterError,
  uploadTrainingPlanFile,
} from '../middlewares/trainingPlanMiddleware';

const router = express.Router();

router.post(
  '/',
  authenticate,
  uploadTrainingPlanFile.fields([{ name: 'image', maxCount: 1 }]),
  handleMulterError,
  checkFileSizes,
  addTrainingPlan,
);

export default router;
