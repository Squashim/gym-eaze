import mongoose, { Types } from "mongoose";
import privacySettings, { PrivacySettings } from "../constants/privacySettings";
import advancedLevel, { AdvancedLevel } from "../constants/advancedLevel";
import { IExercise } from "../models/exerciseModel";

export interface ITrainingPlan extends mongoose.Document {
  title: string;
  description?: string;
  image?: string;
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
      // to jest wymagane w koncu?
      minLength: 1,
    },
    image: {
      type: String,
      // dal bym bez default
      default: "",
    },
    visibility: {
      type: String,
      enum: privacySettings,
      default: privacySettings[0],
      required: true,
    },
    advancedLevel: {
      type: String,
      enum: advancedLevel,
      default: advancedLevel[0],
      required: true,
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
            // tak samo minLength, skoro jest required
            minLength: 1,
            required: true,
          },
          restBetweenExercises: {
            type: Number,
            // dal bym defaulta jakiegos nawet na te 30 sekund
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
                      // chyba ciezar byl opcjonalny
                      required: true,
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
    // timestampy
  },
  { timestamps: true },
);

const TrainingPlan = mongoose.model<ITrainingPlan>(
  "TrainingPlan",
  trainingSchema,
);

export default TrainingPlan;
