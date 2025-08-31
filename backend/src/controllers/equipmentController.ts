import { Request, Response } from 'express';
import * as equipmentService from '../services/equipmentService';

export const getAllEquipment = async (req: Request, res: Response): Promise<void> => {
  try {
    const equipment = await equipmentService.getAllEquipment();
    res.json(equipment);
  } catch (err: any) {
    res.status(500).json({ message: "Błąd serwera", error: err.message });
  }
};

export const getEquipmentById = async (req: Request, res: Response): Promise<void> => {
  try {
    const equipment = await equipmentService.getEquipmentById(req.params.id);

    if (!equipment) {
      res.status(404).json({ message: "Nie znaleziono sprzętu" });
      return;
    }

    res.json(equipment);
  } catch (err: any) {
    res.status(500).json({ message: "Błąd serwera", error: err.message });
  }
};