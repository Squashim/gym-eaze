import express from 'express';
import { authenticate } from '../middlewares/authMiddleware';
import {
  completeDayTraining,
  deleteStartedTrainingPlan,
  getStartedTrainingPlanDayDetails,
  getStartedTrainingPlanDays,
  getStartedTrainingPlans,
  resetStartedTrainingPlan,
  startTrainingPlan,
} from '../controllers/userHasTrainingPlansController';

const router = express.Router();

// Lista wszystkich rozpoczętych treningów z pełnymi informacjami (overview)
router.get('/user/:userId', authenticate, getStartedTrainingPlans);

// Rozpocznij nowy trening
router.post('/:trainingPlanId/start', authenticate, startTrainingPlan);

// Usuń rozpoczęty trening (nie pokazuje się już w /started)
router.delete('/:userTrainingPlanId', authenticate, deleteStartedTrainingPlan);

// Zresetuj rozpoczęty trening (pokaze sie w /started ale completedDays = 0)
router.patch('/:userTrainingPlanId/reset', authenticate, resetStartedTrainingPlan);

// Pobierz wszystkie dni w rozpoczętym treningu
router.get('/:userTrainingPlanId/days', authenticate, getStartedTrainingPlanDays);

// Pobierz szczegóły konkretnego dnia (ćwiczenia + serie)
router.get('/:userTrainingPlanId/days/:dayNumber', authenticate, getStartedTrainingPlanDayDetails);

// Oznacza dzień jako ukończony
router.patch('/:userTrainingPlanId/days/:dayNumber/complete', authenticate, completeDayTraining);

export default router;
