import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import { createReport } from '../controllers/reportController';

const router = express.Router();

router.post('/', authenticate, createReport);

export default router;
