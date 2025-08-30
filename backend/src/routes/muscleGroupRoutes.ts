import express from 'express';
import { getAllMuscleGroups, getMuscleGroupById } from '../controllers/muscleGroupController';

const router = express.Router();

router.get("/", getAllMuscleGroups);
router.get("/:id", getMuscleGroupById);

export default router;
