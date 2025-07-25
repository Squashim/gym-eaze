import mongoose, { Types } from "mongoose";
import { AdvancedLevel, advancedLevelValues } from "../constants/advancedLevel";
import { IMuscleGroup } from "../models/muscleGroup";
import { IEquipment } from "../models/equipmentModel";
import { IUser } from "../models/userModel";
import {
  PrivacySettings,
  privacySettingsValues,
} from "../constants/privacySettings";

export interface IExercise extends mongoose.Document {
  author: Types.ObjectId | IUser;
  name: string;
  description?: string;
  image?: string;
  video?: string;
  likes: number;
  visibility: PrivacySettings;
  advancedLevel: AdvancedLevel;
  muscleGroup: {
    type: Types.ObjectId | IMuscleGroup;
  }[];
  equipment: { type: Types.ObjectId | IEquipment }[];
  hashtags?: string[];
  createdAt: Date;
  updatedAt: Date;
}

const exerciseSchema = new mongoose.Schema(
  {
    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    name: {
      type: String,
      maxLength: 150,
      required: true,
    },
    description: {
      maxLength: 850,
      type: String,
    },
    image: {
      type: String,
      default: "",
    },
    video: {
      type: String,
      default: "",
    },
    likes: {
      type: Number,
      default: 0,
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
      required: true,
    },
    muscleGroup: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: "MuscleGroup",
        required: true,
      },
    ],
    equipment: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Equipment",
        required: true,
      },
    ],
    hashtags: [
      {
        type: String,
        maxLength: 35,
      },
    ],
  },
  { timestamps: true },
);

const Exercise = mongoose.model<IExercise>("Exercise", exerciseSchema);

export default Exercise;
