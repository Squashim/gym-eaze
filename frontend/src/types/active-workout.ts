import type { AdvancedLevel } from '@/constants/enums/advanced-level';
import type { Goal } from '@/constants/enums/goals';
import type { WorkoutStatus } from '@/constants/enums/workout-status';

export type GetActiveWorkoutsApiResponse = {
  _id: string;
  originalId: string;
  name: string;
  image: string;
  goal: Goal;
  advancedLevel: AdvancedLevel;
  author: {
    _id: string;
    username: string;
    profilePicture?: string;
  };
  status: WorkoutStatus;
  totalDays: number;
  trainingDays: number;
  restDays: number;
  completedDays: number;
  createdAt: string;
};
