import { FilterQuery, Types } from 'mongoose';
import { CreateReportPayload, ReportFilters } from '../types/report';
import { parseDescription } from '../utils/parsers';
import { CommentParentType, commentParentTypeValues } from '../constants/commentType';
import Exercise from '../models/exerciseModel';
import TrainingPlan from '../models/trainingPlanModel';
import Post from '../models/postModel';
import Comment from '../models/commentModel';
import Report, { IReport } from '../models/reportModel';
import { ReportType, reportTypeValues } from '../constants/reportType';
import { PageSize } from '../types/trainingPlan';
import User from '../models/userModel';
import { UserRole } from '../constants/roles';
import { StatusReport, statusReportValues } from '../constants/statuses';
import { sendNotificationService } from './notificationService';
import { Notifications } from '../constants/notifications';

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

  await report.save();
};

export const getAllReportsService = async (
  filters: ReportFilters,
  page: number,
  pageSize: PageSize,
  userId: string,
) => {
  const query: FilterQuery<IReport> = {};
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (user.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (filters.status) {
    query.status = Array.isArray(filters.status) ? { $in: filters.status } : filters.status;
  }

  if (filters.reportedItemType) {
    query.reportedItemType = Array.isArray(filters.reportedItemType)
      ? { $in: filters.reportedItemType }
      : filters.reportedItemType;
  }

  const skip = (page - 1) * pageSize;

  const groupedReports = await Report.aggregate([
    { $match: query },
    {
      $group: {
        _id: { reportedItemId: '$reportedItemId', reportedItemType: '$reportedItemType' },
        count: { $sum: 1 },
        status: { $first: '$status' },
        lastReportedAt: { $max: '$createdAt' },
        reviewedAt: { $first: '$reviewedAt' },
      },
    },
    {
      $addFields: {
        statusOrder: {
          $switch: {
            branches: [
              { case: { $eq: ['$status', 'Oczekujący'] }, then: 0 },
              { case: { $eq: ['$status', 'Rozwiązany'] }, then: 1 },
              { case: { $eq: ['$status', 'Odrzucony'] }, then: 2 },
            ],
            default: 3,
          },
        },
      },
    },
    { $sort: { statusOrder: 1, count: -1, lastReportedAt: -1 } },
    { $skip: skip },
    { $limit: pageSize },
    {
      $project: {
        _id: 0,
        count: 1,
        status: 1,
        lastReportedAt: 1,
        reviewedAt: 1,
        reportedItemId: '$_id.reportedItemId',
        reportedItemType: '$_id.reportedItemType',
      },
    },
  ]);

  for (const group of groupedReports) {
    const { reportedItemId, reportedItemType } = group;
    let reportedItem = null;

    switch (reportedItemType) {
      case CommentParentType.exercise:
        reportedItem = await Exercise.findById(reportedItemId)
          .populate('author', 'username profileDetails.profilePicture isBlocked')
          .select('name author createdAt');
        break;
      case CommentParentType.training:
        reportedItem = await TrainingPlan.findById(reportedItemId)
          .populate('author', 'username profileDetails.profilePicture isBlocked')
          .select('name author createdAt');
        break;
      case CommentParentType.post:
        reportedItem = await Post.findById(reportedItemId)
          .populate('author', 'username profileDetails.profilePicture isBlocked')
          .select('content author createdAt');
        break;
      case CommentParentType.comment:
        reportedItem = await Comment.findById(reportedItemId)
          .populate('author', 'username profileDetails.profilePicture isBlocked')
          .select('content author parentType parentId createdAt');

        if (reportedItem && reportedItem.parentType === CommentParentType.comment) {
          let parentComment = null;
          parentComment = await Comment.findById(reportedItem.parentId).select(
            'parentType parentId',
          );

          if (parentComment) {
            reportedItem.parentId = parentComment.parentId;
            reportedItem.parentType = parentComment.parentType;
          }
        }
        break;
    }

    group.reportedItem = reportedItem;
  }

  const totalGroups = await Report.aggregate([
    { $match: query },
    {
      $group: {
        _id: { reportedItemId: '$reportedItemId', reportedItemType: '$reportedItemType' },
      },
    },
    { $count: 'total' },
  ]);

  const total = totalGroups[0]?.total || 0;
  return { items: groupedReports, total, page, pageSize };
};

export const getReportsForItemService = async (
  userId: string,
  reportedItemId: string,
  reportedItemType: string,
) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (user.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (!Types.ObjectId.isValid(reportedItemId)) {
    throw new Error('Nieprawidłowy identyfikator podmiotu');
  }

  if (!commentParentTypeValues.includes(reportedItemType as CommentParentType)) {
    throw new Error('Nieprawidłowy typ podmiotu');
  }

  const reports = await Report.find({ reportedItemId, reportedItemType })
    .sort({ createdAt: -1 })
    .populate([{ path: 'reporterId', select: 'username profileDetails.profilePicture' }])
    .select('reporterId reportType reason createdAt');

  return reports;
};

export const updateReportStatusForItemService = async (
  userId: string,
  reportedItemId: string,
  reportedItemType: string,
  status: string,
) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (user.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (!Types.ObjectId.isValid(reportedItemId)) {
    throw new Error('Nieprawidłowy identyfikator podmiotu');
  }

  if (!commentParentTypeValues.includes(reportedItemType as CommentParentType)) {
    throw new Error('Nieprawidłowy typ podmiotu');
  }

  if (!statusReportValues.includes(status as StatusReport)) {
    throw new Error('Nieprawidłowy status zgłoszenia');
  }

  if (status === StatusReport.pending) {
    throw new Error('Nie można ustawić statusu na Oczekujący');
  }

  const currentReports = await Report.find({
    reportedItemId,
    reportedItemType,
  });

  if (!currentReports || currentReports.length === 0) {
    throw new Error('REPORTS_NOT_FOUND');
  }

  const nonPendingReports = currentReports.filter(
    (report) => report.status !== StatusReport.pending,
  );

  if (nonPendingReports.length > 0) {
    throw new Error(
      'Nie można zmienić statusu. Niektóre zgłoszenia mają już status inny niż Oczekujący',
    );
  }

  const result = await Report.updateMany(
    {
      reportedItemId,
      reportedItemType,
      status: StatusReport.pending,
    },
    { status },
  );

  if (result.matchedCount === 0) {
    throw new Error('REPORTS_NOT_FOUND');
  }

  if (status === StatusReport.resolved) {
    let itemDetails = '';
    switch (currentReports[0].reportedItemType) {
      case CommentParentType.exercise: {
        const exercise = await Exercise.findById(reportedItemId).select('name');
        itemDetails = exercise
          ? `Ćwiczenie: "${exercise.name.length > 50 ? exercise.name.slice(0, 50) + '...' : exercise.name}"`
          : 'Ćwiczenie';
        break;
      }
      case CommentParentType.training: {
        const training = await TrainingPlan.findById(reportedItemId).select('name');
        itemDetails = training
          ? `Plan treningowy: "${training.name.length > 50 ? training.name.slice(0, 50) + '...' : training.name}"`
          : 'Plan treningowy';
        break;
      }
      case CommentParentType.post: {
        const post = await Post.findById(reportedItemId).select('content ');
        itemDetails = post
          ? `Post: "${post.content.length > 50 ? post.content.slice(0, 50) + '...' : post.content}"`
          : 'Post';
        break;
      }
      case CommentParentType.comment: {
        const comment = await Comment.findById(reportedItemId).select('content');
        itemDetails = comment
          ? `Komentarz: "${comment.content.length > 50 ? comment.content.slice(0, 50) + '...' : comment.content}"`
          : 'Komentarz';
        break;
      }
    }

    const reporters = await Report.find({
      reportedItemId,
      reportedItemType,
    }).distinct('reporterId');

    for (const reporterId of reporters) {
      await sendNotificationService(
        `Twoje zgłoszenie dotyczące (${itemDetails}) zostało rozpatrzone pozytywnie. Dziękujemy za pomoc w dbaniu o społeczność!`,
        reporterId.toString(),
        Notifications.info,
        'System',
      );
    }
  }

  return {
    message: 'Zaktualizowano status zgłoszeń',
  };
};

export const deleteReportsForItemService = async (
  userId: string,
  reportedItemId: string,
  reportedItemType: string,
) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (user.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (!Types.ObjectId.isValid(reportedItemId)) {
    throw new Error('Nieprawidłowy identyfikator podmiotu');
  }

  if (!commentParentTypeValues.includes(reportedItemType as CommentParentType)) {
    throw new Error('Nieprawidłowy typ podmiotu');
  }

  const currentReports = await Report.find({
    reportedItemId,
    reportedItemType,
  });

  if (!currentReports || currentReports.length === 0) {
    throw new Error('REPORTS_NOT_FOUND');
  }

  const pendingReports = currentReports.filter((report) => report.status === StatusReport.pending);

  if (pendingReports.length > 0) {
    throw new Error(
      `Nie można usunąć zgłoszeń. ${pendingReports.length} zgłoszeń ma status Oczekujący`,
    );
  }

  const result = await Report.deleteMany({
    reportedItemId,
    reportedItemType,
    status: { $ne: StatusReport.pending },
  });

  if (result.deletedCount === 0) {
    throw new Error('REPORTS_NOT_FOUND');
  }

  return {
    message: 'Usunięto zgłoszenia',
  };
};
