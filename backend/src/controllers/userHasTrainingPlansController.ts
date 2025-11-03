import { Response } from 'express';
import { AuthRequest } from '../types/auth';
import {
  completeDayTrainingService,
  deleteStartedTrainingPlanService,
  getStartedTrainingPlanDayDetailsService,
  getStartedTrainingPlanDaysService,
  getStartedTrainingPlansService,
  resetStartedTrainingPlanService,
  startTrainingPlanService,
} from '../services/userHasTrainingPlansService';

export const getStartedTrainingPlans = async (req: AuthRequest, res: Response) => {
  try {
    const { userId: targetUserId } = req.params;
    const currentUserId = req.userId;
    const includePrivate =
      typeof req.query.includePrivate === 'undefined' ? true : req.query.includePrivate === 'true';
    const page = req.query.page ? Number(req.query.page) : 1;
    const pageSize = req.query.pageSize ? Number(req.query.pageSize) : 10;

    const result = await getStartedTrainingPlansService(
      targetUserId,
      page,
      pageSize,
      includePrivate,
      currentUserId!,
    );
    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const startTrainingPlan = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { trainingPlanId } = req.params;

    const result = await startTrainingPlanService(userId, trainingPlanId);
    return res.status(201).json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const deleteStartedTrainingPlan = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { userTrainingPlanId } = req.params;

    await deleteStartedTrainingPlanService(userId, userTrainingPlanId);
    return res.json({ message: 'Plan treningowy został usunięty' });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const resetStartedTrainingPlan = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { userTrainingPlanId } = req.params;

    const result = await resetStartedTrainingPlanService(userId, userTrainingPlanId);
    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getStartedTrainingPlanDays = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { userTrainingPlanId } = req.params;

    const result = await getStartedTrainingPlanDaysService(userId, userTrainingPlanId);
    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getStartedTrainingPlanDayDetails = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { userTrainingPlanId, dayNumber } = req.params;

    const result = await getStartedTrainingPlanDayDetailsService(
      userId,
      userTrainingPlanId,
      Number(dayNumber),
    );
    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const completeDayTraining = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId!;
    const { userTrainingPlanId, dayNumber } = req.params;

    const result = await completeDayTrainingService(userId, userTrainingPlanId, Number(dayNumber));
    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};
