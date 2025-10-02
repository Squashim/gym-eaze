import mongoose, { FilterQuery, Types } from 'mongoose';
import { uploadTrainingPlanMedia } from '../config/cloudinary';
import TrainingPlan, { ITrainingPlan } from '../models/trainingPlanModel';
import {
  CreateTrainingPlanData,
  PageSize,
  SortBy,
  TrainingPlanFilters,
  UpdateTrainingPlanData,
} from '../types/trainingPlan';
import {
  calculateExerciseDuration,
  validateTrainingPlanData,
  validateTrainingPlanFile,
} from '../utils/trainingPlanValidation';
import { PrivacySettings } from '../constants/privacySettings';
import User from '../models/userModel';

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

  if (!plan) {
    throw new Error('NOT_FOUND');
  }

  if (plan.author.toString() !== userId.toString()) {
    return false;
  }

  await plan.deleteOne();
  return true;
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
    TrainingPlan.find(query)
      .sort({ createdAt: -1, _id: -1 })
      .skip(skip)
      .limit(pageSize)
      .populate('author', 'username profileDetails.profilePicture')
      .lean(),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const updateTrainingPlanService = async (
  planId: string,
  updateData: Partial<UpdateTrainingPlanData>,
  imageBuffer?: Buffer,
  userId?: string,
) => {
  const existingPlan = await TrainingPlan.findById(planId);
  if (!existingPlan) {
    throw new Error('Plan treningowy nie został znaleziony');
  }

  if (existingPlan.author.toString() !== userId) {
    throw new Error('Brak uprawnień do edycji tego planu');
  }

  const finalUpdateData: any = {};

  if (updateData.name !== undefined) finalUpdateData.name = updateData.name;
  if (updateData.description !== undefined) finalUpdateData.description = updateData.description;
  if (updateData.goal !== undefined) finalUpdateData.goal = updateData.goal;
  if (updateData.visibility !== undefined) finalUpdateData.visibility = updateData.visibility;
  if (updateData.advancedLevel !== undefined)
    finalUpdateData.advancedLevel = updateData.advancedLevel;
  if (updateData.hashtags !== undefined) finalUpdateData.hashtags = updateData.hashtags;

  if (updateData.days) {
    const daysWithDuration = updateData.days.map((day) => {
      const exercisesWithDuration = day.exercises.map((exercise, idx, arr) => {
        const isLastExercise = idx === arr.length - 1;
        const duration = calculateExerciseDuration(exercise as any, isLastExercise);
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

    finalUpdateData.days = daysWithDuration;
  }

  if (imageBuffer) {
    validateTrainingPlanFile(imageBuffer);
    const { imageUrl } = await uploadTrainingPlanMedia(imageBuffer);
    finalUpdateData.image = imageUrl;
  }

  if (Object.keys(finalUpdateData).length > 0) {
    const dataToValidate = {
      name: finalUpdateData.name || existingPlan.name,
      goal: finalUpdateData.goal || existingPlan.goal,
      visibility: finalUpdateData.visibility || existingPlan.visibility,
      advancedLevel: finalUpdateData.advancedLevel || existingPlan.advancedLevel,
      days: finalUpdateData.days || existingPlan.days,
      hashtags: finalUpdateData.hashtags || existingPlan.hashtags,
    };

    validateTrainingPlanData(dataToValidate);
  }

  const updatedPlan = await TrainingPlan.findByIdAndUpdate(
    planId,
    { $set: finalUpdateData },
    { new: true, runValidators: true },
  );

  return updatedPlan;
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
  const trainingPlan = await TrainingPlan.findById(planId)
    .populate('author', 'username profileDetails.profilePicture')
    .populate({
      path: 'days.exercises.id',
      select: 'name image muscleGroup equipment advancedLevel description',
      populate: [
        {
          path: 'muscleGroup',
          select: 'name image',
        },
        {
          path: 'equipment',
          select: 'name image',
        },
      ],
    })
    .lean();

  if (!trainingPlan) {
    return null;
  }

  const isAuthor = currentUserId && trainingPlan.author._id.toString() === currentUserId;
  const isPublic = trainingPlan.visibility === PrivacySettings.public;

  if (!isPublic && !isAuthor) {
    throw new Error('Nie masz uprawnień do przeglądania tego planu treningowego');
  }

  const daysCount = trainingPlan.days?.length || 0;
  const totalExercises =
    trainingPlan.days?.reduce((sum, day) => sum + (day.exercises?.length || 0), 0) || 0;
  const totalDuration =
    trainingPlan.days?.reduce((sum, day) => sum + (day.totalDuration || 0), 0) || 0;

  return {
    ...trainingPlan,
    daysCount,
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
  if (userId) {
    query.author = { $ne: userId };
  }

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

  if (filters.duration) {
    const daysCount = filters.duration;
    andFilters.push({ days: { $size: daysCount } });
  }

  if (filters.minDuration !== undefined || filters.maxDuration !== undefined) {
    const durationFilter: any = {};

    if (filters.minDuration !== undefined) {
      durationFilter.$gte = filters.minDuration;
    }
    if (filters.maxDuration !== undefined) {
      durationFilter.$lte = filters.maxDuration;
    }
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

  const projection = {
    name: 1,
    image: 1,
    goal: 1,
    advancedLevel: 1,
    hashtags: 1,
    likes: 1,
    createdAt: 1,
    author: 1,
    'days.dayNumber': 1,
    'days.totalDuration': 1,
    'days.exercises': 1,
  };

  const [items, total] = await Promise.all([
    TrainingPlan.find(query)
      .select(projection)
      .sort(sort)
      .skip(skip)
      .limit(pageSize)
      .populate('author', 'username profileDetails.profilePicture')
      .lean()
      .then((plans) =>
        plans.map((plan) => ({
          ...plan,
          daysCount: plan.days?.length || 0,
          totalExercises:
            plan.days?.reduce((sum, day) => sum + (day.exercises?.length || 0), 0) || 0,
          totalDuration: plan.days?.reduce((sum, day) => sum + (day.totalDuration || 0), 0) || 0,
        })),
      ),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const getTrainingPlansByUserService = async (
  targetUserId: string,
  page: number,
  pageSize: PageSize,
  currentUserId?: string,
) => {
  const query: FilterQuery<ITrainingPlan> = { author: targetUserId };

  if (!currentUserId || currentUserId !== targetUserId) {
    query.visibility = PrivacySettings.public;
  }

  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    TrainingPlan.find(query)
      .sort({ createdAt: -1, _id: -1 })
      .skip(skip)
      .limit(pageSize)
      .populate('author', 'username profileDetails.profilePicture')
      .lean(),
    TrainingPlan.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};
