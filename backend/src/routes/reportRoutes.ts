import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {
  createReport,
  deleteReportsForItem,
  getAllReports,
  getReportsForItem,
  updateReportStatusForItem,
} from '../controllers/reportController';

const router = express.Router();

router.post('/', authenticate, createReport);

router.post('/search', authenticate, getAllReports);

router.get('/item/:reportedItemId/:reportedItemType', authenticate, getReportsForItem);

router.patch(
  '/item/:reportedItemId/:reportedItemType/status',
  authenticate,
  updateReportStatusForItem,
);

router.delete('/item/:reportedItemId/:reportedItemType', authenticate, deleteReportsForItem);

export default router;
