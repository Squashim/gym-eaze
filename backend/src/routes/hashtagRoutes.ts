import express from 'express';
import { getAllHashtags, getPopularHashtags } from '../controllers/HashtagController';

const router = express.Router();

router.get("/", getAllHashtags);
router.get("/popular", getPopularHashtags);

export default router;