import { Types } from 'mongoose';
import { CreateReportPayload } from '../types/report';
import { parseDescription } from '../utils/parsers';
import { CommentParentType } from '../constants/commentType';
import Exercise from '../models/exerciseModel';
import TrainingPlan from '../models/trainingPlanModel';
import Post from '../models/postModel';
import Comment from '../models/commentModel';
import Report from '../models/reportModel';
import { ReportType, reportTypeValues } from '../constants/reportType';

export const createReportService = async (data: CreateReportPayload, userId: string) => {
  const { reportedItemId, reportedItemType, reportType, reason } = data;

  if (!reportedItemId || !reportedItemType || !reportType) {
    throw new Error(
      'Nie podano wszystkich wymaganych pól zgłoszenia - (reportedItemId, reportedItemType, reportType)',
    );
  }

  if (!Types.ObjectId.isValid(reportedItemId)) {
    throw new Error('Nieprawidłowy identyfikator zgłaszanego podmiotu');
  }

  if (!reportTypeValues.includes(reportType)) {
    throw new Error('Nieprawidłowy typ zgłoszenia');
  }

  const alreadyReported = await Report.exists({
    reporterId: userId,
    reportedItemId,
    reportedItemType,
    reportType,
  });

  if (alreadyReported) {
    throw new Error('Już zgłosiłeś ten obiekt w tym typie zgłoszenia');
  }

  if (reportType === ReportType.other) {
    if (!reason || parseDescription(reason).length > 250) {
      throw new Error('Powód zgłoszenia jest wymagany i może mieć maksymalnie 250 znaków');
    }
  } else if (reason) {
    delete data.reason;
  }

  let itemExists = false;
  switch (reportedItemType) {
    case CommentParentType.exercise:
      itemExists = !!(await Exercise.exists({ _id: reportedItemId }));
      break;
    case CommentParentType.training:
      itemExists = !!(await TrainingPlan.exists({ _id: reportedItemId }));
      break;
    case CommentParentType.post:
      itemExists = !!(await Post.exists({ _id: reportedItemId }));
      break;
    case CommentParentType.comment:
      itemExists = !!(await Comment.exists({ _id: reportedItemId }));
  }
  if (!itemExists) {
    throw new Error('Nie znaleziono zgłaszanego podmiotu');
  }

  const report = new Report({
    reporterId: userId,
    ...data,
  });

  return await report.save();
};
