import { AdvancedLevel } from './advanced-level';
import { Goal } from './goals';
import { Visibility } from './visibility';

const exerciseCreatorSteps = ['Podstawowe dane', 'Dodatkowe informacje', 'Podsumowanie'];

const exerciseCreatorDefaultValues = {
  stepOne: {
    name: '',
    image: undefined,
    video: undefined,
    visibility: Visibility.public,
    advancedLevel: AdvancedLevel.none,
  },
  stepTwo: {
    description: '',
    muscleGroup: [],
    equipment: [],
    hashtags: [],
  },
};

export const exerciseCreatorConstants = {
  steps: exerciseCreatorSteps,
  defaultValues: exerciseCreatorDefaultValues,
};

const workoutCreatorSteps = ['Podstawowe dane', 'Dni treningowe', 'Podsumowanie'];

const workoutCreatorDefaultValues = {
  stepOne: {
    name: '',
    image: undefined,
    advancedLevel: AdvancedLevel.none,
    visibility: Visibility.public,
    trainingGoal: Goal.none,
    description: '',
    hashtags: [],
  },
  stepTwo: {
    days: [
      {
        name: 'Dzień 1',
        isRestDay: false,
        exercises: [],
      },
    ],
  },
};

export const workoutCreatorConstants = {
  steps: workoutCreatorSteps,
  defaultValues: workoutCreatorDefaultValues,
};
