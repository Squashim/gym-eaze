import mongoose, { Types } from "mongoose";
import {
  PrivacySettings,
  privacySettingsValues,
} from "../constants/privacySettings";
import { AdvancedLevel, advancedLevelValues } from "../constants/advancedLevel";
import { IExercise } from "../models/exerciseModel";
import { Goal, goalValues } from "../constants/goals";

export interface ITrainingPlan extends mongoose.Document {
  title: string;
  description?: string;
  image?: string;
  goal: Goal;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  likes: number;
  days: {
    day: {
      name: string;
      restBetweenExercises: number;
      totalDuration?: number;
      isRestDay: boolean;
      exercises: {
        exercise: {
          id: Types.ObjectId | IExercise;
          duration?: number;
          sets: {
            repetitions: number;
            weight: number;
            restAfterSet: number;
          }[];
        };
      }[];
    };
  }[];
  hashtags?: string[];
  createdAt: Date;
  updatedAt: Date;
}

const trainingSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      maxLength: 150,
      minLength: 1,
      required: true,
    },
    description: {
      type: String,
      maxLength: 800,
    },
    image: {
      type: String,
      default: "",
    },
    goal: {
      type: String,
      enum: goalValues,
      default: Goal.None,
    },
    visibility: {
      type: String,
      enum: privacySettingsValues,
      default: PrivacySettings.Publiczny,
      required: true,
    },
    advancedLevel: {
      type: String,
      enum: advancedLevelValues,
      default: AdvancedLevel.Easy,
    },
    likes: {
      type: Number,
      default: 0,
    },
    days: [
      {
        day: {
          name: {
            type: String,
            maxLength: 150,
            required: true,
          },
          restBetweenExercises: {
            type: Number,
            default: 30,
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
              exercise: {
                id: {
                  type: mongoose.Schema.Types.ObjectId,
                  ref: "Exercise",
                  required: true,
                },
                duration: {
                  type: Number,
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
                    restAfterSet: {
                      type: Number,
                      required: true,
                    },
                  },
                ],
              },
            },
          ],
        },
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

const TrainingPlan = mongoose.model<ITrainingPlan>(
  "TrainingPlan",
  trainingSchema,
);

export default TrainingPlan;
