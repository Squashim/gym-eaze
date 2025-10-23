import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {
  deleteNotification,
  getNotifications,
  sendNotification,
} from '../controllers/notificationController';

const router = express.Router();

router.get('/', authenticate, getNotifications);
router.post('/', authenticate, sendNotification);
router.delete('/', authenticate, deleteNotification);

export default router;
