import { AdvancedLevel } from './advanced-level';
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
