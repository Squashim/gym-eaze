import type { AdvancedLevel } from '@/constants/advanced-level';
import type { Visibility } from '@/constants/visibility';

type BaseExercise<TImage = string, TVideo = string> = {
  author: string;
  name: string;
  description: string;
  image: TImage;
  video: TVideo;
  visibility: Visibility;
  advancedLevel: AdvancedLevel;
  muscleGroup: string[];
  equipment: string[];
  hashtags?: string[];
};

export type Exercise = BaseExercise<string, string> & {
  _id: string;
  likes: number;
  createdAt: string;
  updatedAt: string;
};

export type CreateExercisePayload = BaseExercise<File, File>;
