import { Router } from "express";
import { addExercise } from "../controllers/exerciseController";
import { uploadExerciseFiles, handleMulterError, checkFileSizes } from "../middlewares/exerciseMiddleware";
import { authenticate } from "../middlewares/authMiddleware";
import { deleteExercise } from "../controllers/exerciseController";

const router = Router();

router.post(
  "/",
  authenticate,
  uploadExerciseFiles.fields([
    { name: "image", maxCount: 1 }, 
    { name: "video", maxCount: 1 }
  ]),
  handleMulterError,
  checkFileSizes,
  addExercise
);

router.delete("/:id", authenticate, deleteExercise);

export default router;