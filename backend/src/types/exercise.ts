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
  muscleGroups?: string[];
  equipment?: string[];
  hashtags?: string[];
  difficulty?: AdvancedLevel;
  followers?: string[];
  favorites?: string[];
};

export type UpdateExerciseData = Partial<CreateExerciseData>;
