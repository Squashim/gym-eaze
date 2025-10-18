import mongoose, { Types } from 'mongoose';
import { ITrainingPlan } from './trainingPlanModel';
import { IExercise } from './exerciseModel';
import { Goal } from '../constants/goals';
import { AdvancedLevel } from '../constants/advancedLevel';
import { Set } from '../types/trainingPlan';

export interface IUserHasTrainingPlans extends mongoose.Document {
  userId: Types.ObjectId;
  trainingPlanSnapshot: {
    originalId: Types.ObjectId;
    name: string;
    description?: string;
    image: string;
    goal: Goal;
    advancedLevel: AdvancedLevel;
    author: {
      id: Types.ObjectId;
      username: string;
      profilePicture?: string;
    };
    days: {
      name: string;
      dayNumber: number;
      totalDuration: number;
      isRestDay: boolean;
      exercises: {
        originalId: Types.ObjectId;
        name: string;
        description: string;
        image: string;
        video: string;
        advancedLevel: AdvancedLevel;
        muscleGroup: { id: Types.ObjectId; name: string }[];
        equipment: { id: Types.ObjectId; name: string }[];
        isRepsSet: boolean;
        duration: number;
        restAfterSet: number;
        sets: Set[];
      }[];
    }[];
  };
  completedAt?: Date;
  progress: {
    totalDays: number;
    completedDays: {
      dayNumber: number;
      exercises: {
        exerciseOriginalId: Types.ObjectId;
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
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    trainingPlanSnapshot: {
      originalId: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
      },
      name: {
        type: String,
        required: true,
      },
      description: {
        type: String,
      },
      image: {
        type: String,
        required: true,
      },
      goal: {
        type: String,
        required: true,
      },
      advancedLevel: {
        type: String,
        required: true,
      },
      author: {
        id: {
          type: mongoose.Schema.Types.ObjectId,
          required: true,
        },
        username: {
          type: String,
          required: true,
        },
        profilePicture: {
          type: String,
        },
      },
      days: [
        {
          name: {
            type: String,
            required: true,
          },
          dayNumber: {
            type: Number,
            required: true,
          },
          totalDuration: {
            type: Number,
            required: true,
          },
          isRestDay: {
            type: Boolean,
            required: true,
          },
          exercises: [
            {
              originalId: {
                type: mongoose.Schema.Types.ObjectId,
                required: true,
              },
              name: {
                type: String,
                required: true,
              },
              description: {
                type: String,
                required: true,
              },
              image: {
                type: String,
                required: true,
              },
              video: {
                type: String,
                required: true,
              },
              advancedLevel: {
                type: String,
                required: true,
              },
              muscleGroup: [
                {
                  id: {
                    type: mongoose.Schema.Types.ObjectId,
                    required: true,
                  },
                  name: {
                    type: String,
                    required: true,
                  },
                },
              ],
              equipment: [
                {
                  id: {
                    type: mongoose.Schema.Types.ObjectId,
                    required: true,
                  },
                  name: {
                    type: String,
                    required: true,
                  },
                },
              ],
              isRepsSet: {
                type: Boolean,
                required: true,
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
                  type: mongoose.Schema.Types.Mixed,
                  required: true,
                },
              ],
            },
          ],
        },
      ],
    },
    completedAt: {
      type: Date,
    },
    progress: {
      totalDays: {
        type: Number,
        required: true,
        default: 0,
      },
      completedDays: [
        {
          dayNumber: {
            type: Number,
            required: true,
          },
          exercises: [
            {
              exerciseOriginalId: {
                type: mongoose.Schema.Types.ObjectId,
                required: true,
              },
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
                  _id: false,
                },
              ],
              _id: false,
            },
          ],
          _id: false,
        },
      ],
    },
  },
  { timestamps: true },
);

const UserHasTrainingPlans = mongoose.model<IUserHasTrainingPlans>(
  'UserHasTrainingPlans',
  userHasTrainingPlansSchema,
);

export default UserHasTrainingPlans;
