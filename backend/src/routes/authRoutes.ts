import express from 'express';
import { login, logout, me, refresh, register } from '../controllers/authController';
import { googleAuth } from '../controllers/authController';
import { authenticate, nonAuthenticate } from '../middlewares/authMiddleware';

const router = express.Router();

//Publiczne
router.post('/register', nonAuthenticate, register);
router.post('/login', nonAuthenticate, login);
router.post('/refresh', refresh);
router.post('/google', googleAuth);

//Chronione
router.get('/me', authenticate, me);
router.post('/logout', authenticate, logout);

export default router;
