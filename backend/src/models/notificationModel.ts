import mongoose, { Types } from "mongoose";
import notifications, { Notifications } from "../constants/notifications";
import { IUser } from "../models/userModel";

export interface INotification extends mongoose.Document {
  sender: Types.ObjectId | IUser;
  message: string;
  type: Notifications;
  createdAt: Date;
  updatedAt: Date;
}

const notificationSchema = new mongoose.Schema(
  {
    sender: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
    },
    message: {
      type: String,
      required: true,
    },
    type: {
      type: String,
      enum: notifications,
      default: notifications[0],
    },
  },
  { timestamps: true },
);

const Notification = mongoose.model<INotification>(
  "Notification",
  notificationSchema,
);

export default Notification;
