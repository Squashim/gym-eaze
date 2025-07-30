import mongoose, { Types } from "mongoose";
import { ITrainingPlan } from "../models/trainingPlanModel";
import { IExercise } from "../models/exerciseModel";

export interface IUserHasTrainingPlans extends mongoose.Document {
  trainingPlanId: Types.ObjectId | ITrainingPlan;
  completedAt?: Date;
  progress: {
    totalDays: number;
    completedDays: {
      day: Types.ObjectId;
      exercises: {
        id: Types.ObjectId | IExercise;
        sets: {
          balancePercentage?: number;
          tempoPercentage?: number;
          time?: number;
        }[];
      }[];
    }[];
  };
  createdAt: Date;
  updatedAt: Date;
}

const userHasTrainingPlansSchema = new mongoose.Schema(
  {
    trainingPlanId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "TrainingPlan",
      required: true,
    },
    completedAt: {
      type: Date,
    },
    progress: {
      totalDays: {
        type: Number,
        default: 0,
        required: true,
      },
      completedDays: [
        {
          day: {
            type: Types.ObjectId,
            required: true,
            exerciseId: [
              {
                type: mongoose.Schema.Types.ObjectId,
                ref: "Exercise",
                required: true,
                sets: [
                  {
                    balancePercentage: {
                      type: Number,
                    },
                    tempoPercentage: {
                      type: Number,
                    },
                    time: {
                      type: Number,
                    },
                  },
                ],
              },
            ],
          },
          _id: false,
        },
      ],
    },
  },
  { timestamps: true },
);

const UserHasTrainingPlans = mongoose.model<IUserHasTrainingPlans>(
  "UserHasTrainingPlans",
  userHasTrainingPlansSchema,
);

export default UserHasTrainingPlans;
