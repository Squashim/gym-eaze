import Exercise, { IExercise } from '../models/exerciseModel';
import { deleteFromCloudinary, uploadExerciseMedia } from '../config/cloudinary';
import { validateExerciseData, validateExercisePatchData } from '../utils/exerciseValidation';
import mongoose, { FilterQuery, Types } from 'mongoose';
import User from '../models/userModel';
import {
  CreateExerciseData,
  ExerciseFilters,
  PageSize,
  SortBy,
  TrainingPlanExercise,
  UpdateExerciseData,
} from '../types/exercise';
import { PrivacySettings } from '../constants/privacySettings';
import { UserRole } from '../constants/roles';
import { CommentParentType } from '../constants/commentType';
import { deleteCommentsAndLikes } from '../utils/helpers';
import { sendNotificationService } from './notificationService';
import { Notifications } from '../constants/notifications';
import { checkAndEarnBadges } from './badgeService';

export const createExercise = async (
  data: CreateExerciseData,
  imageBuffer: Buffer,
  videoBuffer: Buffer,
) => {
  validateExerciseData(data);

  const { imageUrl, videoUrl } = await uploadExerciseMedia(imageBuffer, videoBuffer);

  const exercise = new Exercise({
    ...data,
    image: imageUrl,
    video: videoUrl,
  });

  const createdExercise = await exercise.save();

  await checkAndEarnBadges(data.author.toString(), 'Exercises');

  return createdExercise;
};

export const deleteExercise = async (exerciseId: string, userId: string) => {
  const exercise = await Exercise.findById(exerciseId);
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  if (!exercise) {
    throw new Error('EXERCISE_NOT_FOUND');
  }

  if (user.role !== UserRole.admin && exercise.author.toString() !== userId.toString()) {
    throw new Error('FORBIDDEN');
  }

  const usersWithFavorite = await User.find({
    'favorites.exercises': exercise._id,
  }).select('_id');

  await deleteCommentsAndLikes(
    CommentParentType.exercise,
    (exercise._id as Types.ObjectId).toString(),
  );

  await User.updateMany(
    { 'likes.exercises': exercise._id },
    { $pull: { 'likes.exercises': exercise._id } },
  );

  await User.updateMany(
    { 'favorites.exercises': exercise._id },
    { $pull: { 'favorites.exercises': exercise._id } },
  );

  for (const favUser of usersWithFavorite) {
    await sendNotificationService(
      `Ćwiczenie "${exercise.name}" zostało usunięte z platformy, więc zostało również usunięte z Twoich ulubionych. Przepraszamy za niedogodności.`,
      favUser._id.toString(),
      Notifications.info,
      'System',
    );
  }

  await exercise.deleteOne();
};

export const updateExercise = async (
  exerciseId: string,
  userId: string,
  data: UpdateExerciseData,
  imageBuffer?: Buffer,
  videoBuffer?: Buffer,
) => {
  const exercise = await Exercise.findById(exerciseId);

  if (!exercise) {
    throw new Error('NOT_FOUND');
  }

  if (exercise.author.toString() !== userId.toString()) {
    return false;
  }

  validateExercisePatchData(data);

  let imageUrl = exercise.image;
  let videoUrl = exercise.video;

  if (imageBuffer) {
    const uploaded = await uploadExerciseMedia(imageBuffer, undefined);
    await deleteFromCloudinary(imageUrl);
    if (uploaded.imageUrl) imageUrl = uploaded.imageUrl;
  }

  if (videoBuffer) {
    const uploaded = await uploadExerciseMedia(undefined, videoBuffer);
    await deleteFromCloudinary(videoUrl, 'video');
    if (uploaded.videoUrl) videoUrl = uploaded.videoUrl;
  }

  Object.assign(exercise, {
    name: data.name || exercise.name,
    description: data.description || exercise.description,
    visibility: data.visibility || exercise.visibility,
    advancedLevel: data.advancedLevel || exercise.advancedLevel,
    muscleGroup: data.muscleGroup || exercise.muscleGroup,
    equipment: data.equipment || exercise.equipment,
    hashtags: data.hashtags || exercise.hashtags,
    image: imageUrl,
    video: videoUrl,
    updatedAt: new Date(),
  });

  return await exercise.save();
};

export const toggleExerciseLikeService = async (userId: string, exerciseId: string) => {
  if (!mongoose.Types.ObjectId.isValid(exerciseId)) {
    throw new Error('Nieprawidłowe ID ćwiczenia');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const exercise = await Exercise.findById(exerciseId);
  if (!exercise) {
    throw new Error('Nie znaleziono ćwiczenia');
  }

  const isLiked = user.likes.exercises.some((fav) => fav.toString() === exerciseId);

  if (isLiked) {
    user.likes.exercises = user.likes.exercises.filter((fav) => fav.toString() !== exerciseId);
    exercise.likes = Math.max(0, (exercise.likes || 0) - 1);
    await user.save();
    await exercise.save();
    return { message: 'Usunięto polubienie', likes: exercise.likes };
  } else {
    user.likes.exercises.push(exercise._id as Types.ObjectId);
    exercise.likes = (exercise.likes || 0) + 1;
    await user.save();
    await exercise.save();

    if (exercise.author._id.toString() !== user._id.toString()) {
      await sendNotificationService(
        `Użytkownik "${user.username}" polubił Twoje ćwiczenie "${exercise.name}".`,
        exercise.author._id.toString(),
        Notifications.info,
        user.username,
      );
    }

    return { message: 'Dodano polubienie', likes: exercise.likes };
  }
};

export const toggleExerciseFavoriteService = async (userId: string, exerciseId: string) => {
  if (!mongoose.Types.ObjectId.isValid(exerciseId)) {
    throw new Error('Nieprawidłowe ID ćwiczenia');
  }

  const exercise = await Exercise.findById(exerciseId);
  if (!exercise) {
    throw new Error('Ćwiczenie nie istnieje');
  }

  if (exercise.author.toString() === userId.toString()) {
    throw new Error('Nie możesz dodać własnego ćwiczenia do ulubionych');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const isFavorite = user.favorites.exercises.some(
    (fav) => fav.toString() === exerciseId.toString(),
  );

  if (isFavorite) {
    user.favorites.exercises = user.favorites.exercises.filter(
      (fav) => fav.toString() !== exerciseId.toString(),
    );
    await user.save();
    return { message: 'Usunięto ćwiczenie z ulubionych' };
  } else {
    user.favorites.exercises.push(exercise._id as mongoose.Types.ObjectId);
    await user.save();
    return { message: 'Dodano ćwiczenie do ulubionych' };
  }
};

export const getExerciseByIdService = async (
  id: string,
  userId?: string,
): Promise<typeof exercise | null | 'forbidden'> => {
  if (!mongoose.Types.ObjectId.isValid(id)) {
    throw new Error('Nieprawidłowy identyfikator ćwiczenia');
  }

  const exercise = await Exercise.findById(id)
    .populate([
      { path: 'muscleGroup' },
      { path: 'equipment' },
      { path: 'author', select: 'username profileDetails.profilePicture' },
    ])
    .lean();

  if (!exercise) {
    return null;
  }

  if (exercise.visibility === PrivacySettings.public) {
    return exercise;
  }

  if (exercise.visibility === PrivacySettings.private) {
    if (userId && exercise.author._id.toString() === userId.toString()) {
      return exercise;
    }
    return 'forbidden';
  }

  return null;
};

export const getExercisesService = async (
  filters: ExerciseFilters,
  sortBy: SortBy,
  page: number,
  pageSize: PageSize,
  userId?: string,
) => {
  const query: FilterQuery<IExercise> = {};

  query.visibility = PrivacySettings.public;

  const andFilters: FilterQuery<IExercise>[] = [];

  if (filters.search?.trim()) {
    andFilters.push({ name: { $regex: filters.search.trim(), $options: 'i' } });
  }

  if (filters.muscleGroups?.length) andFilters.push({ muscleGroup: { $in: filters.muscleGroups } });
  if (filters.equipment?.length) andFilters.push({ equipment: { $in: filters.equipment } });
  if (filters.hashtags?.length) andFilters.push({ hashtags: { $in: filters.hashtags } });
  if (filters.difficulty) andFilters.push({ advancedLevel: filters.difficulty });

  if (filters.followers && userId) {
    const user = await User.findById(userId).select('followedUsers').lean();
    const followedUsers = user?.followedUsers ?? [];

    andFilters.push({ author: { $in: followedUsers } });
  }

  if (andFilters.length) {
    query.$and = andFilters;
  }

  type SortType = Partial<Record<keyof IExercise, 1 | -1>>;
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
    Exercise.find(query)
      .sort(sort)
      .skip(skip)
      .limit(pageSize)
      .populate([
        { path: 'muscleGroup', select: 'name' },
        { path: 'equipment', select: 'name' },
        { path: 'author', select: 'username profileDetails.profilePicture' },
      ]),
    Exercise.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const getExercisesByUserService = async (
  targetUserId: string,
  page: number,
  pageSize: PageSize,
  includePrivate: boolean,
  currentUserId?: string,
) => {
  const query: FilterQuery<IExercise> = { author: targetUserId };

  if (!currentUserId || currentUserId !== targetUserId) {
    query.visibility = PrivacySettings.public;
  } else if (currentUserId === targetUserId && !includePrivate) {
    query.visibility = PrivacySettings.public;
  }

  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    Exercise.find(query)
      .sort({ createdAt: -1, _id: -1 })
      .skip(skip)
      .limit(pageSize)
      .populate([
        { path: 'muscleGroup', select: 'name' },
        { path: 'equipment', select: 'name' },
        { path: 'author', select: 'username profileDetails.profilePicture' },
      ]),
    Exercise.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};

export const getExercisesByIdsService = async (ids: string[]) => {
  const invalidId = ids.find((id) => !mongoose.Types.ObjectId.isValid(id));

  if (invalidId) {
    throw new Error(`Nieprawidłowy identyfikator ćwiczenia`);
  }

  const exercises = await Exercise.find({ _id: { $in: ids } })
    .select('image name muscleGroup')
    .populate('muscleGroup', 'name')
    .lean<TrainingPlanExercise[]>();

  return exercises;
};

export const getFavoriteExercisesService = async (
  userId: string,
  page: number,
  pageSize: PageSize,
) => {
  const user = await User.findById(userId).select('favorites.exercises').lean();

  if (!user || !user.favorites || !user.favorites.exercises?.length) {
    return { items: [], total: 0, page, pageSize };
  }

  const query: FilterQuery<IExercise> = { _id: { $in: user.favorites.exercises } };
  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    Exercise.find(query)
      .sort({ createdAt: -1, _id: -1 })
      .skip(skip)
      .limit(pageSize)
      .populate([
        { path: 'muscleGroup', select: 'name' },
        { path: 'equipment', select: 'name' },
        { path: 'author', select: 'username profileDetails.profilePicture' },
      ]),
    Exercise.countDocuments(query),
  ]);

  return { items, total, page, pageSize };
};
