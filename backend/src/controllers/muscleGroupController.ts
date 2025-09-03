import { Request, Response } from 'express';
import * as muscleGroupService from '../services/muscleGroupService';

export const getAllMuscleGroups = async (req: Request, res: Response): Promise<void> => {
  try {
    const muscleGroups = await muscleGroupService.getAllMuscleGroups();
    res.json(muscleGroups);
  } catch {
    res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const getMuscleGroupById = async (req: Request, res: Response): Promise<void> => {
  try {
    const muscleGroup = await muscleGroupService.getMuscleGroupById(req.params.id);

    if (!muscleGroup) {
      res.status(404).json({ message: 'Nie znaleziono grupy mięśniowej' });
      return;
    }

    res.json(muscleGroup);
  } catch {
    res.status(500).json({ message: 'Błąd serwera' });
  }
};
