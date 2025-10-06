import { Request } from 'express';
import { AdvancedLevel } from '../constants/advancedLevel';
import { PrivacySettings } from '../constants/privacySettings';

export interface ExerciseRequest extends Request {
  files?: {
    image?: Express.Multer.File[];
    video?: Express.Multer.File[];
  };
}

export type CreateExerciseData = {
  author: string;
  name: string;
  description: string;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  muscleGroup: string[];
  equipment: string[];
  hashtags?: string[];
};

export type ExerciseFilters = {
  search?: string;
  muscleGroups?: string[];
  equipment?: string[];
  hashtags?: string[];
  difficulty?: AdvancedLevel;
  followers?: boolean;
};

export type SortBy = 'popular' | 'top-rated' | 'latest';
export type PageSize = 9 | 18 | 36;

export type ExerciseQuery = {
  filters?: ExerciseFilters;
  sortBy: SortBy;
  page: number;
  pageSize: PageSize;
};

export type TrainingPlanExercise = {
  _id: string;
  name: string;
  image: string;
  muscleGroup: {
    _id: string;
    name: string;
  }[];
};

export type UpdateExerciseData = Partial<CreateExerciseData>;
