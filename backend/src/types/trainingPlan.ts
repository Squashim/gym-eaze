import { Types } from 'mongoose';
import { IExercise } from '../models/exerciseModel';
import { Request } from 'express';
import { Goal } from '../constants/goals';
import { PrivacySettings } from '../constants/privacySettings';
import { AdvancedLevel } from '../constants/advancedLevel';

export interface DayExercise {
  id: Types.ObjectId | IExercise;
  isRepsSet: boolean;
  duration?: number;
  restAfterSet: number;
  sets: {
    repetitions: number;
    weight: number;
  }[];
}

export interface TrainingDay {
  name: string;
  isRestDay: boolean;
  exercises: DayExercise[];
}

export interface CreateTrainingPlanData {
  title: string;
  author: string;
  description?: string;
  goal: Goal;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  hashtags?: string[];
  days: TrainingDay[];
}

export interface TrainingPlanRequest extends Request {
  body: CreateTrainingPlanData;
  files?: {
    image?: Express.Multer.File[];
  };
}
