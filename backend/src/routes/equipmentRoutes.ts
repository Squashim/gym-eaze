import express from 'express';
import { getAllEquipment, getEquipmentById } from '../controllers/equipmentController';

const router = express.Router();

router.get("/", getAllEquipment);
router.get("/:id", getEquipmentById);

export default router;
