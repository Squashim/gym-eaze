import { Types } from 'mongoose';
import { IExercise } from '../models/exerciseModel';
import { Request } from 'express';
import { Goal } from '../constants/goals';
import { PrivacySettings } from '../constants/privacySettings';
import { AdvancedLevel } from '../constants/advancedLevel';

export type RepsSet = { repetitions: number; weight?: number | null };
export type DurationSet = { duration: number };
export type Set = RepsSet | DurationSet;

export type DayExercise =
  | {
      id: Types.ObjectId | IExercise;
      isRepsSet: true;
      restAfterSet: number;
      sets: RepsSet[];
    }
  | {
      id: Types.ObjectId | IExercise;
      isRepsSet: false;
      restAfterSet: number;
      sets: DurationSet[];
    };

export interface TrainingDay {
  name: string;
  dayNumber: number;
  isRestDay: boolean;
  exercises: DayExercise[];
}

export interface CreateTrainingPlanData {
  name: string;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  goal: Goal;
  days: TrainingDay[];
  description?: string;
  hashtags?: string[];
}

export interface TrainingPlanRequest extends Request {
  body: CreateTrainingPlanData;
  files?: {
    image?: Express.Multer.File[];
  };
}

export interface TrainingPlanFilters {
  search?: string;
  goal?: Goal;
  difficulty?: AdvancedLevel;
  duration?: number;
  minDuration?: number;
  maxDuration?: number;
  hashtags?: string[];
  followers?: boolean;
}

export interface TrainingPlanQuery {
  filters?: TrainingPlanFilters;
  sortBy?: SortBy;
  page?: number;
  pageSize?: PageSize;
}

export type SortBy = 'latest' | 'popular' | 'top-rated';
export type PageSize = 9 | 18 | 27;

export interface UpdateTrainingPlanData {
  name?: string;
  description?: string;
  goal?: Goal;
  visibility?: PrivacySettings;
  advancedLevel?: AdvancedLevel;
  hashtags?: string[];
  days?: Array<{
    name: string;
    dayNumber: number;
    isRestDay: boolean;
    exercises: Array<{
      id: Types.ObjectId | IExercise;
      isRepsSet: boolean;
      restAfterSet: number;
      sets: Set[];
    }>;
  }>;
}
