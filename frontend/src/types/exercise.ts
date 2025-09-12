import type { AdvancedLevel } from '@/constants/advanced-level';
import type { Visibility } from '@/constants/visibility';
import type { MuscleGroup } from './muscle-group';
import type { Equipment } from './equipment';
import type { Author } from './user';

type BaseExercise<
  TMuscleGroup = string[],
  TEquipment = string[],
  TImage = string,
  TVideo = string,
> = {
  name: string;
  description: string;
  image: TImage;
  video: TVideo;
  visibility: Visibility;
  advancedLevel: AdvancedLevel;
  muscleGroup: TMuscleGroup;
  equipment: TEquipment;
  hashtags?: string[];
};

export type BaseExerciseApiResponse<
  TMuscleGroup = string[],
  TEquipment = string[],
  TAuthor = string,
> = BaseExercise<TMuscleGroup, TEquipment> & {
  _id: string;
  author: TAuthor;
  likes: number;
  createdAt: string;
  updatedAt: string;
};

export type GetExerciseApiResponse = BaseExerciseApiResponse<MuscleGroup[], Equipment[], Author>;

export type GetExercisesApiResponse = BaseExerciseApiResponse<{ _id: string; name: string }[]>;

export type CreateExercisePayload = BaseExercise<string[], string[], File, File>;

export type UpdateExercisePayload = Partial<CreateExercisePayload>;
