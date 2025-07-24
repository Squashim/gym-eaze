import mongoose, { Types } from "mongoose";
import privacySettings from "../constants/privacySettings";
import advancedLevel, { AdvancedLevel } from "../constants/advancedLevel";
import { IMuscleGroup } from "../models/muscleGroup";
import { IEquipment } from "../models/equipmentModel";
import { IHashtag } from "../models/hashtagsModel";
import { IUser } from "../models/userModel";

export interface IExercise extends mongoose.Document {
  // jesli nie ma koniecznosci populate, np. equipment, author, muscleGroup
  // czyli nie bedziemy wyswietlac wiecej danych o autorze, ... przy cwiczeniu to mozna dawac bez interfejsow | IUser itd...
  // ale jak beda populate to mozna zostawic <3
  author: Types.ObjectId | IUser;
  name: string;
  description?: string;
  image?: string;
  video?: string;
  likes: number;
  // visibility jako enum
  visibility: string;
  advancedLevel: AdvancedLevel;
  muscleGroup: {
    type: Types.ObjectId | IMuscleGroup;
  }[];
  equipment: { type: Types.ObjectId | IEquipment }[];
  // Hasztagi jako string, bo zmienilismy
  hashtags?: { type: Types.ObjectId | IHashtag }[];
  // timestampy
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
    // image i video chyba nie musza miec default skoro sa i tak opcjonalne
    // wiec sie nie pojawia w bazie po prostu jak beda puste
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
        type: mongoose.Schema.Types.ObjectId,
        ref: "Hashtag",
      },
    ],
  },
  { timestamps: true },
);

const Exercise = mongoose.model<IExercise>("Exercise", exerciseSchema);

export default Exercise;
