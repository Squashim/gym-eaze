import type { AdvancedLevel } from '@/constants/advanced-level';
import type { Goal } from '@/constants/goals';
import type { Visibility } from '@/constants/visibility';

export interface CreateWorkoutPayload {
  name: string;
  image: File;
  visibility: Visibility;
  advancedLevel: AdvancedLevel;
  goal: Goal;
  days: {
    numberDay: number;
    name: string;
    isRestDay: boolean;
    exercises: {
      id: string;
      isRepsSet: boolean;
      duration?: number;
      sets: {
        repetitions: number;
        weight?: number;
        restAfterSet?: number;
      };
    }[];
  }[];
  description?: string;
  hashtags?: string[];
}
