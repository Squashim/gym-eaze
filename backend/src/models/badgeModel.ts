import mongoose from "mongoose";
import { BadgeActions, badgeActionValues } from "../constants/badgeActions";

export interface IBadge extends Document {
  title: string;
  description: string;
  image: string;
  requirements: {
    action: BadgeActions;
    target?: number;
    isConnected?: boolean;
  };
}

const badgeSchema = new mongoose.Schema({
  title: {
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
  requirements: {
    action: {
      type: String,
      enum: badgeActionValues,
      required: true,
    },
    target: {
      type: Number,
      default: 0,
    },
    isConnected: {
      type: Boolean,
      default: false,
    },
  },
});

const Badge = mongoose.model<IBadge>("Badge", badgeSchema);

export default Badge;
