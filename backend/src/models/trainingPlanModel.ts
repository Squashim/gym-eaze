import mongoose, { Types } from 'mongoose';
import { PrivacySettings, privacySettingsValues } from '../constants/privacySettings';
import { AdvancedLevel, advancedLevelValues } from '../constants/advancedLevel';
import { IExercise } from '../models/exerciseModel';
import { Goal, goalValues } from '../constants/goals';
import { IUser } from './userModel';
import { Set } from '../types/trainingPlan';

export interface ITrainingPlan extends mongoose.Document {
  name: string;
  author: Types.ObjectId | IUser;
  description?: string;
  image: string;
  goal: Goal;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  likes: number;
  days: {
    name: string;
    dayNumber: number;
    totalDuration: number;
    isRestDay: boolean;
    exercises: {
      id: Types.ObjectId | IExercise;
      isRepsSet: boolean;
      duration: number;
      restAfterSet: number;
      sets: Set[];
    }[];
  }[];
  hashtags?: string[];
  createdAt: Date;
  updatedAt: Date;
}

const exerciseSchema = new mongoose.Schema(
  {
    id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Exercise',
      required: true,
    },
    isRepsSet: {
      type: Boolean,
      required: true,
      default: true,
    },
    duration: {
      type: Number,
    },
    restAfterSet: {
      type: Number,
      required: true,
    },
    sets: {
      type: [
        {
          type: mongoose.Schema.Types.Mixed,
          required: true,
        },
      ],
      validate: {
        validator: function (sets: Set[]) {
          return sets.every(
            (set) =>
              ('repetitions' in set && !('duration' in set)) ||
              ('duration' in set && !('repetitions' in set)),
          );
        },
        message: 'Każda seria musi być albo powtórzeniowa, albo czasowa.',
      },
    },
  },
  {
    _id: false,
  },
);

const daySchema = new mongoose.Schema(
  {
    dayNumber: {
      type: Number,
      default: 0,
      required: true,
    },
    name: {
      type: String,
      maxLength: 100,
      required: true,
    },
    totalDuration: {
      type: Number,
      required: true,
    },
    isRestDay: {
      type: Boolean,
      required: true,
      default: false,
    },
    exercises: [exerciseSchema],
  },
  { _id: false },
);

const trainingSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      maxLength: 150,
      minLength: 1,
      required: true,
    },
    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    description: {
      type: String,
      validate: {
        validator: function (v: string) {
          const normalized = v.replace(/\r\n/g, '\n');
          return normalized.length <= 850;
        },
        message: 'Opis może mieć maksymalnie 850 znaków',
      },
    },
    image: {
      type: String,
      default: '',
      required: true,
    },
    goal: {
      type: String,
      enum: goalValues,
      default: Goal.none,
    },
    visibility: {
      type: String,
      enum: privacySettingsValues,
      default: PrivacySettings.public,
      required: true,
    },
    advancedLevel: {
      type: String,
      enum: advancedLevelValues,
      default: AdvancedLevel.easy,
      required: true,
    },
    likes: {
      type: Number,
      default: 0,
    },
    days: [daySchema],
    hashtags: [
      {
        type: String,
      },
    ],
  },
  { timestamps: true },
);

const TrainingPlan = mongoose.model<ITrainingPlan>('TrainingPlan', trainingSchema);

export default TrainingPlan;
