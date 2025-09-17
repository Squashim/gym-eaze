import { Request, Response } from 'express';
import { AuthRequest } from '../types/auth';
import { TrainingPlanRequest } from '../types/trainingPlan';
import { TrainingPlanValidationError } from '../utils/trainingPlanValidation';
import { createTrainingPlan } from '../services/trainingPlanService';

export const addTrainingPlan = async (req: Request, res: Response) => {
  const trainingReq = req as TrainingPlanRequest;
  const authReq = req as AuthRequest;

  try {
    const { title, description, goal, visibility, advancedLevel, hashtags } = trainingReq.body;
    let { days } = trainingReq.body;
    const userId = authReq.userId;
    const imageFile = trainingReq.files?.image?.[0];

    if (!imageFile) {
      return res.status(400).json({ message: 'Zdjęcie jest wymagane' });
    }

    if (typeof days === 'string') {
      try {
        days = JSON.parse(days);
      } catch {
        return res.status(400).json({ message: 'Nieprawidłowy format dni' });
      }
    }

    const trainingPlan = await createTrainingPlan(
      {
        author: userId!,
        title: title.trim(),
        description: description?.trim(),
        goal,
        visibility,
        advancedLevel,
        hashtags,
        days,
      },
      imageFile.buffer,
    );

    return res.status(201).json(trainingPlan);
  } catch (error) {
    if (error instanceof TrainingPlanValidationError) {
      return res.status(400).json({ message: error.message });
    }

    console.error('Training plan creation error:', error);
    return res.status(500).json({
      message: 'Błąd podczas dodawania planu treningowego',
    });
  }
};
