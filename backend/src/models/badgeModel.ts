import mongoose from "mongoose";
import { badgeActions, BadgeAction } from "../constants/badgeActions";

export interface IBadge extends Document {
  title: string;
  description: string;
  image: string;
  requirements: {
    action: BadgeAction;
    target?: number;
    connected?: boolean;
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
      enum: badgeActions,
      required: true,
    },
    target: {
      type: Number,
      default: 0,
    },
    connected: {
      type: Boolean,
      default: false,
    },
  },
});

const Badge = mongoose.model<IBadge>("Badge", badgeSchema);

export default Badge;
