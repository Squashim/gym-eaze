import { AdvancedLevel } from './advanced-level';
import { Goal } from './goals';
import { Visibility } from './visibility';

const IMAGE_MIME_TYPES = ['image/png', 'image/jpeg', 'image/webp'];
const IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB

const VIDEO_MIME_TYPES = ['video/mp4', 'video/webm'];
const VIDEO_MAX_SIZE = 20 * 1024 * 1024; // 20MB

export const MEDIA = {
  IMAGE_MIME_TYPES,
  IMAGE_MAX_SIZE,
  VIDEO_MIME_TYPES,
  VIDEO_MAX_SIZE,
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
