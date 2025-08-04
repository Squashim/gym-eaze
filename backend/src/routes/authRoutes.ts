import express from "express";
import {
  login,
  logout,
  refresh,
  register,
} from "../controllers/authController";
import { googleAuth } from "../controllers/authController";

const router = express.Router();
router.post("/register", register);
router.post("/login", login);
router.post("/refresh", refresh);
router.post("/logout", logout);
router.post("/google", googleAuth);

export default router;
