import type { AdvancedLevel } from '@/constants/advanced-level';
import type { Goal } from '@/constants/goals';
import type { Visibility } from '@/constants/visibility';
import type { Author } from './user';

export type RepsSet = { repetitions: number; weight?: number | null };
export type DurationSet = { duration: number };
export type WorkoutSets = RepsSet | DurationSet;
export type WorkoutWeek = {
  name: string;
  days: WorkoutDay[];
};

export interface WorkoutSheetTabProps {
  onExerciseToggle: (id: string) => void;
  isExerciseAdded: (id: string) => boolean;
}

type BaseWorkout<TImage = string, TDay = WorkoutDay[]> = {
  name: string;
  image: TImage;
  visibility: Visibility;
  advancedLevel: AdvancedLevel;
  goal: Goal;
  days: TDay;
  description?: string;
  hashtags?: string[];
};

export type CreateWorkoutPayload = BaseWorkout<File, WorkoutDay[]>;

export type UpdateWorkoutPayload = Partial<CreateWorkoutPayload>;

export type WorkoutApiResponse = BaseWorkout<string, WorkoutApiDay[]> & {
  _id: string;
  author: Author;
  likes: number;
  createdAt: string;
  updatedAt: string;
};

type WorkoutApiDay = {
  name: string;
  dayNumber: number;
  totalDuration: number;
  isRestDay: boolean;
  exercises: WorkoutApiExercise[];
};

type WorkoutApiExercise =
  | {
      id: string;
      isRepsSet: true;
      duration: number;
      restAfterSet: number;
      sets: RepsSet[];
    }
  | {
      isRepsSet: false;
      id: string;
      duration: number;
      restAfterSet: number;
      sets: DurationSet[];
    };

export type WorkoutDay = {
  name: string;
  dayNumber: number;
  isRestDay: boolean;
  exercises: WorkoutExercise[];
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
