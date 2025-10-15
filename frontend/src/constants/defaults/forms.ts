import { AdvancedLevel } from '../enums/advanced-level';
import { Country } from '../enums/countries';
import { Gender } from '../enums/gender';
import { Goal } from '../enums/goals';
import { Visibility } from '../enums/visibility';

export const loginDefaultValues = {
  login: '',
  password: '',
  rememberMe: false,
};

export const signupDefaultValues = {
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
};

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
    goal: Goal.none,
    description: '',
    hashtags: [],
  },
  stepTwo: {
    days: [
      {
        name: 'Dzień 1',
        dayNumber: 0,
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

export const postCreatorDefaultValues = {
  content: '',
  image: undefined,
  video: undefined,
  hashtags: [],
};

export const loginDetailsDefaultValues = {
  username: '',
  password: '',
  confirmPassword: '',
};

export const fitnessDetailsDefaultValues = {
  age: undefined,
  weight: undefined,
  height: undefined,
  gender: Gender.none,
};

export const personalDetailsDefaultValues = {
  firstName: undefined,
  lastName: undefined,
  country: Country.NONE,
};

export const profileDetailsDefaultValues = {
  profilePicture: undefined,
  description: '',
  goal: Goal.none,
  pinnedBadges: [],
};

export const privacySettingsDefaultValues = {
  isPersonalDetailsPublic: false,
  isFitnessDetailsPublic: false,
  isBadgesDetailsPublic: false,
  isGoalPublic: false,
  isTrainingStatisticsPublic: false,
};
