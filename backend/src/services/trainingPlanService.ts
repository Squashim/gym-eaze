import mongoose, { FilterQuery, Types } from 'mongoose';
import { deleteFromCloudinary, uploadTrainingPlanMedia } from '../config/cloudinary';
import TrainingPlan, { ITrainingPlan } from '../models/trainingPlanModel';
import {
  CreateTrainingPlanData,
  PageSize,
  SortBy,
  TrainingPlanFilters,
} from '../types/trainingPlan';
import {
  calculateExerciseDuration,
  validateTrainingPlanData,
  validateTrainingPlanFile,
  validateTrainingPlanUpdateData,
} from '../utils/trainingPlanValidation';
import { PrivacySettings } from '../constants/privacySettings';
import User from '../models/userModel';
import puppeteer from 'puppeteer';
import { generateTrainingPlanHTML } from '../utils/trainingPlanPDF';
import { getExercisesByIdsService } from './exerciseService';
import { UserRole } from '../constants/roles';
import { deleteCommentsAndLikes } from '../utils/helpers';
import { CommentParentType } from '../constants/commentType';
import { sendNotificationService } from './notificationService';
import { Notifications } from '../constants/notifications';

export const createTrainingPlan = async (
  data: CreateTrainingPlanData,
  imageBuffer: Buffer,
  userId: string,
) => {
  validateTrainingPlanData(data);
  validateTrainingPlanFile(imageBuffer);

  const { imageUrl } = await uploadTrainingPlanMedia(imageBuffer);

  const daysWithDuration = data.days.map((day) => {
    const exercisesWithDuration = day.exercises.map((exercise, idx, arr) => {
      const isLastExercise = idx === arr.length - 1;
      const duration = calculateExerciseDuration(exercise, isLastExercise ? true : false);
      return {
        ...exercise,
        duration,
      };
    });

    const totalDuration = exercisesWithDuration.reduce(
      (sum, exercise) => sum + exercise.duration,
      0,
    );

    return {
      ...day,
      totalDuration,
      exercises: exercisesWithDuration,
    };
  });

  const trainingPlan = new TrainingPlan({
    ...data,
    author: userId,
    image: imageUrl,
    days: daysWithDuration,
  });

  return await trainingPlan.save();
};

export const deleteTrainingPlan = async (planId: string, userId: string) => {
  const plan = await TrainingPlan.findById(planId);
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (!plan) {
    throw new Error('WORKOUT_NOT_FOUND');
  }

  if (user.role !== UserRole.admin && plan.author.toString() !== userId.toString()) {
    throw new Error('FORBIDDEN');
  }

  await deleteCommentsAndLikes(CommentParentType.training, (plan._id as Types.ObjectId).toString());

  const usersWithFavorite = await User.find({
    'favorites.trainingPlans': plan._id,
  }).select('_id');

  await User.updateMany(
    { 'likes.trainingPlans': plan._id },
    { $pull: { 'likes.trainingPlans': plan._id } },
  );

  await User.updateMany(
    { 'favorites.trainingPlans': plan._id },
    { $pull: { 'favorites.trainingPlans': plan._id } },
  );

  for (const favUser of usersWithFavorite) {
    await sendNotificationService(
      `Plan treningowy "${plan.name}" został usunięty z platformy, więc zostało również usunięty z Twoich ulubionych. Przepraszamy za niedogodności.`,
      favUser._id.toString(),
      Notifications.info,
      'System',
    );
  }

  await plan.deleteOne();
};

export const getFavoriteTrainingPlansService = async (
  userId: string,
  page: number,
  pageSize: PageSize,
) => {
  const user = await User.findById(userId).select('favorites.trainingPlans').lean();

  if (!user || !user.favorites || !user.favorites.trainingPlans?.length) {
    return { items: [], total: 0, page, pageSize };
  }

  const query: FilterQuery<ITrainingPlan> = { _id: { $in: user.favorites.trainingPlans } };
  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    TrainingPlan.aggregate([
      { $match: query },
      ...getTrainingPlanAggregationPipeline(skip, pageSize),
    ]),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const updateTrainingPlanService = async (
  trainingPlan: ITrainingPlan,
  updateData: Partial<CreateTrainingPlanData>,
  imageBuffer?: Buffer,
) => {
  validateTrainingPlanUpdateData(updateData);

  let imageUrl = trainingPlan.image;

  if (imageBuffer) {
    validateTrainingPlanFile(imageBuffer);
    const uploaded = await uploadTrainingPlanMedia(imageBuffer);
    await deleteFromCloudinary(imageUrl);
    if (uploaded.imageUrl) imageUrl = uploaded.imageUrl;
  }

  if (updateData.days) {
    updateData.days = updateData.days.map((day) => {
      const exercisesWithDuration = day.exercises.map((exercise, idx, arr) => {
        const isLastExercise = idx === arr.length - 1;
        const duration = calculateExerciseDuration(exercise, isLastExercise ? true : false);
        return {
          ...exercise,
          duration,
        };
      });

      const totalDuration = exercisesWithDuration.reduce(
        (sum, exercise) => sum + exercise.duration,
        0,
      );

      return { ...day, totalDuration, exercises: exercisesWithDuration };
    });
  }

  Object.assign(trainingPlan, {
    name: updateData.name || trainingPlan.name,
    description: updateData.description || trainingPlan.description,
    goal: updateData.goal || trainingPlan.goal,
    visibility: updateData.visibility || trainingPlan.visibility,
    advancedLevel: updateData.advancedLevel || trainingPlan.advancedLevel,
    hashtags: updateData.hashtags || trainingPlan.hashtags,
    days: updateData.days || trainingPlan.days,
    image: imageUrl,
    updatedAt: new Date(),
  });

  return await trainingPlan.save();
};

export const toggleTrainingPlanLikeService = async (userId: string, planId: string) => {
  if (!mongoose.Types.ObjectId.isValid(planId)) {
    throw new Error('Nieprawidłowe ID planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const plan = await TrainingPlan.findById(planId);
  if (!plan) {
    throw new Error('Nie znaleziono planu treningowego');
  }

  const isLiked = user.likes.trainingPlans.some((fav) => fav.toString() === planId);

  if (isLiked) {
    user.likes.trainingPlans = user.likes.trainingPlans.filter((fav) => fav.toString() !== planId);
    plan.likes = Math.max(0, (plan.likes || 0) - 1);
    await user.save();
    await plan.save();
    return { message: 'Usunięto polubienie', likes: plan.likes };
  } else {
    user.likes.trainingPlans.push(plan._id as Types.ObjectId);
    plan.likes = (plan.likes || 0) + 1;
    await user.save();
    await plan.save();
    return { message: 'Dodano polubienie', likes: plan.likes };
  }
};

export const toggleTrainingPlanFavoriteService = async (userId: string, planId: string) => {
  if (!mongoose.Types.ObjectId.isValid(planId)) {
    throw new Error('Nieprawidłowe ID planu treningowego');
  }

  const plan = await TrainingPlan.findById(planId);
  if (!plan) {
    throw new Error('Plan treningowy nie istnieje');
  }

  if (plan.author.toString() === userId.toString()) {
    throw new Error('Nie możesz dodać własnego planu treningowego do ulubionych');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const isFavorite = user.favorites.trainingPlans.some(
    (fav) => fav.toString() === planId.toString(),
  );

  if (isFavorite) {
    user.favorites.trainingPlans = user.favorites.trainingPlans.filter(
      (fav) => fav.toString() !== planId.toString(),
    );
    await user.save();
    return { message: 'Usunięto plan treningowy z ulubionych' };
  } else {
    user.favorites.trainingPlans.push(plan._id as mongoose.Types.ObjectId);
    await user.save();
    return { message: 'Dodano plan treningowy do ulubionych' };
  }
};

export const getTrainingPlanByIdService = async (planId: string, currentUserId?: string) => {
  if (!mongoose.Types.ObjectId.isValid(planId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const trainingPlan = await TrainingPlan.findById(planId)
    .populate('author', 'username profileDetails.profilePicture')
    .lean();

  if (!trainingPlan) {
    return null;
  }

  const isAuthor = currentUserId && trainingPlan.author._id.toString() === currentUserId;
  const isPublic = trainingPlan.visibility === PrivacySettings.public;

  if (!isPublic && !isAuthor) {
    throw new Error('Nie masz uprawnień do przeglądania tego planu treningowego');
  }

  const totalExercises =
    trainingPlan.days?.reduce((sum, day) => sum + (day.exercises?.length || 0), 0) || 0;
  const totalDuration =
    trainingPlan.days?.reduce((sum, day) => sum + (day.totalDuration || 0), 0) || 0;

  return {
    ...trainingPlan,
    totalExercises,
    totalDuration,
  };
};

export const getTrainingPlansService = async (
  filters: TrainingPlanFilters,
  sortBy: SortBy,
  page: number,
  pageSize: PageSize,
  userId?: string,
) => {
  const query: FilterQuery<ITrainingPlan> = {};

  query.visibility = PrivacySettings.public;

  const andFilters: FilterQuery<ITrainingPlan>[] = [];

  if (filters.search?.trim()) {
    andFilters.push({ name: { $regex: filters.search.trim(), $options: 'i' } });
  }

  if (filters.goal) {
    andFilters.push({ goal: filters.goal });
  }

  if (filters.difficulty) {
    andFilters.push({ advancedLevel: filters.difficulty });
  }

  if (filters.minDuration !== undefined || filters.maxDuration !== undefined) {
    const durationFilter: { $gte?: number; $lte?: number } = {};

    if (filters.minDuration !== undefined) {
      durationFilter.$gte = filters.minDuration;
    }
    if (filters.maxDuration !== undefined) {
      durationFilter.$lte = filters.maxDuration;
    }

    andFilters.push({
      $expr: {
        $and: [
          ...(filters.minDuration !== undefined
            ? [{ $gte: [{ $size: '$days' }, filters.minDuration] }]
            : []),
          ...(filters.maxDuration !== undefined
            ? [{ $lte: [{ $size: '$days' }, filters.maxDuration] }]
            : []),
        ],
      },
    });
  }

  if (filters.hashtags?.length) {
    andFilters.push({ hashtags: { $in: filters.hashtags } });
  }

  if (filters.followers && userId) {
    const user = await User.findById(userId).select('followedUsers').lean();
    const followedUsers = user?.followedUsers ?? [];
    andFilters.push({ author: { $in: followedUsers } });
  }

  if (andFilters.length) {
    query.$and = andFilters;
  }

  type SortType = Partial<Record<keyof ITrainingPlan, 1 | -1>>;
  let sort: SortType = {};

  switch (sortBy) {
    case 'latest':
      sort = { createdAt: -1, _id: -1 };
      break;
    case 'popular': {
      const since = new Date();
      since.setDate(since.getDate() - 1);
      query.createdAt = { $gte: since };
      sort = { likes: -1, _id: -1 };
      break;
    }
    case 'top-rated':
      sort = { likes: -1, _id: -1 };
      break;
    default:
      sort = { createdAt: -1, _id: -1 };
  }

  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    TrainingPlan.aggregate([
      { $match: query },
      ...getTrainingPlanAggregationPipeline(skip, pageSize, sort),
    ]),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const getTrainingPlansByUserService = async (
  targetUserId: string,
  page: number,
  pageSize: PageSize,
  includePrivate: boolean,
  currentUserId?: string,
) => {
  const query: FilterQuery<ITrainingPlan> = { author: new Types.ObjectId(targetUserId) };

  if (!currentUserId || currentUserId !== targetUserId) {
    query.visibility = PrivacySettings.public;
  } else if (currentUserId === targetUserId && !includePrivate) {
    query.visibility = PrivacySettings.public;
  }

  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    TrainingPlan.aggregate([
      { $match: query },
      ...getTrainingPlanAggregationPipeline(skip, pageSize),
    ]),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const generateTrainingPlanPDFService = async (planId: string, userId?: string) => {
  const trainingPlan = await getTrainingPlanByIdService(planId, userId);

  if (!trainingPlan) {
    throw new Error('Plan treningowy nie został znaleziony');
  }

  const exerciseIds: string[] = [];
  trainingPlan.days.forEach((day) => {
    day.exercises.forEach((exercise) => {
      exerciseIds.push(exercise.id.toString());
    });
  });

  const exerciseArray = await getExercisesByIdsService(exerciseIds);
  const exercisesMap: Record<string, unknown> = {};
  exerciseArray.forEach((ex) => {
    exercisesMap[ex._id.toString()] = ex;
  });

  const daysExercises = trainingPlan.days.map((day) => ({
    ...day,
    exercises: day.exercises.map((ex) => ({
      ...ex,
      details: exercisesMap[ex.id.toString()] || null,
    })),
  }));

  const html = generateTrainingPlanHTML(trainingPlan, daysExercises);

  const browser = await puppeteer.launch({
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox'],
  });

  const page = await browser.newPage();
  await page.setContent(html, { waitUntil: 'networkidle0' });

  const pdfBuffer = await page.pdf({
    format: 'A4',
    printBackground: true,
  });

  await browser.close();

  return pdfBuffer;
};

function getTrainingPlanAggregationPipeline(
  skip: number,
  pageSize: number,
  sort: Record<string, 1 | -1> = { createdAt: -1, _id: -1 },
) {
  return [
    {
      $project: {
        name: 1,
        image: 1,
        goal: 1,
        visibility: 1,
        advancedLevel: 1,
        hashtags: 1,
        likes: 1,
        author: 1,
        createdAt: 1,
        daysCount: {
          total: { $size: '$days' },
          rest: {
            $size: {
              $filter: {
                input: '$days',
                cond: { $eq: ['$$this.isRestDay', true] },
              },
            },
          },
          training: {
            $size: {
              $filter: {
                input: '$days',
                cond: { $eq: ['$$this.isRestDay', false] },
              },
            },
          },
        },
        totalExercises: {
          $sum: {
            $map: {
              input: '$days',
              as: 'day',
              in: { $size: { $ifNull: ['$$day.exercises', []] } },
            },
          },
        },
        totalDuration: {
          $sum: '$days.totalDuration',
        },
      },
    },
    { $sort: sort },
    { $skip: skip },
    { $limit: pageSize },
    {
      $lookup: {
        from: 'users',
        localField: 'author',
        foreignField: '_id',
        as: 'author',
        pipeline: [
          {
            $project: {
              username: 1,
              'profileDetails.profilePicture': 1,
            },
          },
        ],
      },
    },
    { $unwind: '$author' },
  ];
}
