import type { AdvancedLevel } from '@/constants/advanced-level';
import type { Goal } from '@/constants/goals';
import type { Visibility } from '@/constants/visibility';

export type RepsSet = { repetitions: number; weight?: number | null };
export type DurationSet = { duration: number };
export type WorkoutSets = RepsSet | DurationSet;

export interface CreateWorkoutPayload {
  name: string;
  image: File;
  visibility: Visibility;
  advancedLevel: AdvancedLevel;
  goal: Goal;
  days: WorkoutDay[];
  description?: string;
  hashtags?: string[];
}

export interface WorkoutSheetTabProps {
  onExerciseToggle: (id: string) => void;
  isExerciseAdded: (id: string) => boolean;
}

export type WorkoutDay = {
  name: string;
  dayNumber: number;
  isRestDay: boolean;
  exercises: WorkoutExercise[];
};

export type WorkoutWeek = {
  name: string;
  days: WorkoutDay[];
};

export type WorkoutExercise =
  | {
      id: string;
      isRepsSet: true;
      restAfterSet: number;
      sets: RepsSet[];
    }
  | {
      isRepsSet: false;
      id: string;
      restAfterSet: number;
      sets: DurationSet[];
    };
