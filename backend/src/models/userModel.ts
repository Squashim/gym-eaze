import mongoose, { Types } from 'mongoose';
import { Country } from '../constants/countries';
import { UserRole, userRoleValues } from '../constants/roles';
import { Gender, genderValues } from '../constants/genders';
import { Goal, goalValues } from '../constants/goals';
import { IBadge } from '../models/badgeModel';
import { IExercise } from '../models/exerciseModel';
import { ITrainingPlan } from '../models/trainingPlanModel';
import { statusesValues, StatusTraining } from '../constants/statuses';
import '../models/notificationModel';
import { IPost } from './postModel';
import { IComment } from './commentModel';
import { IStartedTrainingPlan } from './startedTrainingPlanModel';

export interface IUser extends Document {
  _id: Types.ObjectId;
  googleId?: string;
  username: string;
  email: string;
  password: string;
  lastLoginAt?: Date;
  role: UserRole;
  isBlocked?: boolean;
  likes: {
    trainingPlans: (Types.ObjectId | ITrainingPlan)[];
    exercises: (Types.ObjectId | IExercise)[];
    comments: (Types.ObjectId | IComment)[];
    posts: (Types.ObjectId | IPost)[];
  };
  favorites: {
    trainingPlans: (Types.ObjectId | ITrainingPlan)[];
    exercises: (Types.ObjectId | IExercise)[];
  };
  followedUsers: (Types.ObjectId | IUser)[];
  followers: (Types.ObjectId | IUser)[];
  statistics: {
    startedTrainingPlans: {
      trainingPlanId: Types.ObjectId | IStartedTrainingPlan;
      status: StatusTraining;
    }[];
  };
  profileDetails?: {
    profilePicture: string;
    description: string;
    goal: Goal;
  };
  personalDetails?: {
    firstName: string;
    lastName: string;
    country: Country;
  };
  fitnessDetails?: {
    height?: number;
    weight?: number;
    age?: number;
    gender?: Gender;
  };
  privacySettings: {
    isPersonalDetailsPublic: boolean;
    isFitnessDetailsPublic: boolean;
    isBadgesDetailsPublic: boolean;
    isGoalPublic: boolean;
    isTrainingStatisticsPublic: boolean;
  };
  badges?: {
    badge: Types.ObjectId | IBadge;
    isPinned: boolean;
    awardedAt: Date;
  }[];
  notifications?: Types.ObjectId[];
  createdAt: Date;
  updatedAt: Date;
}

const userSchema = new mongoose.Schema<IUser>(
  {
    googleId: { type: String, unique: true, sparse: true },
    username: {
      type: String,
      required: true,
      unique: true,
    },
    email: {
      type: String,
      required: true,
      unique: true,
    },
    isBlocked: {
      type: Boolean,
    },
    password: {
      type: String,
    },
    lastLoginAt: {
      type: Date,
    },
    role: {
      type: String,
      enum: userRoleValues,
      default: UserRole.user,
      required: true,
    },
    likes: {
      trainingPlans: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'TrainingPlan',
        },
      ],
      exercises: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Exercise',
        },
      ],
      comments: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Comment',
        },
      ],
      posts: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Post',
        },
      ],
    },
    favorites: {
      trainingPlans: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'TrainingPlan',
        },
      ],
      exercises: [
        {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Exercise',
        },
      ],
    },
    followedUsers: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
      },
    ],
    followers: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
      },
    ],
    statistics: {
      totalWeight: {
        type: Number,
        default: 0,
      },
      totalTrainingsTime: {
        type: Number,
        default: 0,
      },
      startedTrainingPlans: [
        {
          trainingPlanId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'UserHasTrainingPlans',
            required: true,
          },
          status: {
            type: String,
            enum: statusesValues,
            default: StatusTraining.active,
            required: true,
          },
          _id: false,
        },
      ],
    },
    profileDetails: {
      profilePicture: {
        type: String,
        default: '',
      },
      description: {
        type: String,
        maxlength: 150,
        default: '',
      },
      goal: {
        type: String,
        enum: goalValues,
        default: Goal.none,
      },
    },

    personalDetails: {
      firstName: {
        type: String,
      },
      lastName: {
        type: String,
      },
      country: {
        type: String,
        enum: Country,
        default: Country.NONE,
      },
    },

    fitnessDetails: {
      height: {
        type: Number,
      },
      weight: {
        type: Number,
      },
      age: {
        type: Number,
      },
      gender: {
        type: String,
        enum: genderValues,
        default: Gender.none,
      },
    },

    privacySettings: {
      isPersonalDetailsPublic: {
        type: Boolean,
        default: true,
      },
      isFitnessDetailsPublic: {
        type: Boolean,
        default: true,
      },
      isBadgesDetailsPublic: {
        type: Boolean,
        default: true,
      },
      isGoalPublic: {
        type: Boolean,
        default: true,
      },
      isTrainingStatisticsPublic: {
        type: Boolean,
        default: true,
      },
    },

    badges: [
      {
        badge: {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Badge',
        },
        isPinned: {
          type: Boolean,
          default: false,
        },
        awardedAt: {
          type: Date,
          default: Date.now,
        },
        _id: false,
      },
    ],

    notifications: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Notification',
      },
    ],
  },
  { timestamps: true },
);

const User = mongoose.model<IUser>('User', userSchema);

export default User;
