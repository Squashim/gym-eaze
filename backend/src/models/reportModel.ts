import mongoose, { Types } from 'mongoose';
import { reportTypeValues, ReportType } from '../constants/reportType';
import { CommentParentType, commentParentTypeValues } from '../constants/commentType';
import { StatusReport, statusReportValues } from '../constants/statuses';

export interface IReport extends Document {
  _id: Types.ObjectId;
  reporterId: Types.ObjectId;
  reportedItemId: Types.ObjectId;
  reportedItemType: CommentParentType;
  reportType: ReportType;
  status: StatusReport;
  reason?: string;
  reviewedAt?: Date;
  updatedAt: Date;
  createdAt: Date;
}

const reportSchema = new mongoose.Schema(
  {
    reporterId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    reportedItemId: {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
    },
    reportedItemType: {
      type: String,
      enum: commentParentTypeValues,
      required: true,
    },
    reportType: {
      type: String,
      enum: reportTypeValues,
      required: true,
    },
    status: {
      type: String,
      enum: statusReportValues,
      default: StatusReport.pending,
    },
    reason: {
      type: String,
      validate: {
        validator: function (v: string) {
          const normalized = v.replace(/\r\n/g, '\n');
          return normalized.length <= 250;
        },
        message: 'Treść może mieć maksymalnie 250 znaków',
      },
    },
    reviewedAt: {
      type: Date,
    },
  },
  {
    timestamps: true,
  },
);

reportSchema.index({ reportedItemId: 1, reportedItemType: 1 });
reportSchema.index({ status: 1 });
reportSchema.index({ reporterId: 1, reportedItemId: 1, reportedItemType: 1, reportType: 1 });

reportSchema.pre('save', function (next) {
  if (this.isModified('status') && this.status !== StatusReport.pending) {
    this.reviewedAt = new Date();
  }
  next();
});

reportSchema.pre(['updateOne', 'updateMany'], function (next) {
  const update = this.getUpdate() as IReport;
  if (update.status && update.status !== StatusReport.pending) {
    update.reviewedAt = new Date();
  }
  next();
});

const Report = mongoose.model<IReport>('Report', reportSchema);

export default Report;
