import type { AdvancedLevel } from '@/constants/enums/advanced-level';
import type { Visibility } from '@/constants/enums/visibility';
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
  commentsCount?: number;
  author: TAuthor;
  likes: number;
  createdAt: string;
  updatedAt: string;
};

export type GetExercisesByIdsApiResponse = {
  _id: string;
  name: string;
  image: string;
  muscleGroup: MuscleGroup[];
};

export type GetExerciseApiResponse = BaseExerciseApiResponse<MuscleGroup[], Equipment[], Author>;

export type GetExercisesApiResponse = BaseExerciseApiResponse<{ _id: string; name: string }[]>;

export type CreateExercisePayload = BaseExercise<string[], string[], File, File>;

export type UpdateExercisePayload = Partial<CreateExercisePayload>;
