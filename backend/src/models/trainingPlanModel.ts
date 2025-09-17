import mongoose, { Types } from 'mongoose';
import { PrivacySettings, privacySettingsValues } from '../constants/privacySettings';
import { AdvancedLevel, advancedLevelValues } from '../constants/advancedLevel';
import { IExercise } from '../models/exerciseModel';
import { Goal, goalValues } from '../constants/goals';
import { IUser } from './userModel';

export interface ITrainingPlan extends mongoose.Document {
  name: string;
  author: Types.ObjectId | IUser;
  description?: string;
  image?: string;
  goal: Goal;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  likes: number;
  days: {
    dayNumber: number;
    name: string;
    totalDuration?: number;
    isRestDay: boolean;
    exercises: {
      id: Types.ObjectId | IExercise;
      isRepsSet: boolean;
      duration?: number;
      restAfterSet: number;
      sets: {
        repetitions: number;
        weight: number;
      }[];
    }[];
  }[];
  hashtags?: string[];
  createdAt: Date;
  updatedAt: Date;
}

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
      maxLength: 850,
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
    },
    likes: {
      type: Number,
      default: 0,
    },
    days: [
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
        },
        isRestDay: {
          type: Boolean,
          default: false,
        },
        exercises: [
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
            sets: [
              {
                repetitions: {
                  type: Number,
                  required: true,
                },
                weight: {
                  type: Number,
                },
                _id: false,
              },
            ],
          },
        ],
      },
    ],
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
