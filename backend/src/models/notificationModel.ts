import mongoose from 'mongoose';
import { Notifications, notificationsValues } from '../constants/notifications';
import { LeanDocument } from '../types/utils';

export interface INotification extends mongoose.Document {
  message: string;
  type: Notifications;
  createdAt: Date;
  updatedAt: Date;
}

const notificationSchema = new mongoose.Schema(
  {
    message: {
      type: String,
      required: true,
    },
    type: {
      type: String,
      enum: notificationsValues,
      default: Notifications.info,
    },
  },
  { timestamps: true },
);

const Notification = mongoose.model<INotification>('Notification', notificationSchema);
export type INotificationSummary = LeanDocument<INotification>;

export default Notification;
