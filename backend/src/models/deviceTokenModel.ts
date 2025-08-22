import mongoose, { Types } from 'mongoose';
import { IUser } from './userModel';

interface IDevice extends mongoose.Document {
  userId: Types.ObjectId | IUser;
  refreshToken: string;
  deviceInfo: string;
  createdAt: Date;
  updatedAt: Date;
}

const deviceTokenSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    refreshToken: {
      type: String,
      required: true,
    },
    deviceInfo: {
      type: String,
      required: true,
    },
  },
  { timestamps: true },
);

const DeviceToken = mongoose.model<IDevice>('DeviceToken', deviceTokenSchema);

export default DeviceToken;
