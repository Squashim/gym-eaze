import Exercise, { IExercise } from '../models/exerciseModel';
import { uploadExerciseMedia } from '../config/cloudinary';
import { validateExerciseData, validateExercisePatchData } from '../utils/exerciseValidation';
import mongoose, { FilterQuery, Types } from 'mongoose';
import User from '../models/userModel';
import { CreateExerciseData, ExerciseFilters, UpdateExerciseData } from '../types/exercise';
import { PrivacySettings } from '../constants/privacySettings';
import { findOrCreateHashtags, incrementUsageCount } from '../services/HashtagService';

export const createExercise = async (
  data: CreateExerciseData,
  imageBuffer: Buffer,
  videoBuffer: Buffer,
) => {
  validateExerciseData(data);

  const { imageUrl, videoUrl } = await uploadExerciseMedia(imageBuffer, videoBuffer);

 
  let hashtagIds: Types.ObjectId[] = [];
  if (data.hashtags && data.hashtags.length > 0) {
    const hashtags = await findOrCreateHashtags(data.hashtags);
    hashtagIds = hashtags.map(h => h._id as Types.ObjectId);
    
    await incrementUsageCount(hashtagIds.map(id => id.toString()));
  }

  const exercise = new Exercise({
    ...data,
    hashtags: hashtagIds,
    image: imageUrl,
    video: videoUrl,
  });

  return await exercise.save();
};

export const deleteExercise = async (exerciseId: string, userId: string) => {
  const exercise = await Exercise.findById(exerciseId);

  if (!exercise) {
    throw new Error('NOT_FOUND');
  }

  if (exercise.author.toString() !== userId.toString()) {
    return false;
  }

  await exercise.deleteOne();
  return true;
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
    if (uploaded.imageUrl) imageUrl = uploaded.imageUrl;
  }

  if (videoBuffer) {
    const uploaded = await uploadExerciseMedia(undefined, videoBuffer);
    if (uploaded.videoUrl) videoUrl = uploaded.videoUrl;
  }

  let hashtagIds = exercise.hashtags;
  if (data.hashtags !== undefined) {
    if (data.hashtags.length > 0) {
      const hashtags = await findOrCreateHashtags(data.hashtags);
      hashtagIds = hashtags.map(h => h._id as Types.ObjectId);
      await incrementUsageCount(hashtagIds.map(id => id.toString()));
    } else {
      hashtagIds = [];
    }
  }

  Object.assign(exercise, {
    name: data.name || exercise.name,
    description: data.description || exercise.description,
    visibility: data.visibility || exercise.visibility,
    advancedLevel: data.advancedLevel || exercise.advancedLevel,
    muscleGroup: data.muscleGroup || exercise.muscleGroup,
    equipment: data.equipment || exercise.equipment,
    hashtags: hashtagIds,
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
  const exercise = await Exercise.findById(id).lean();

  if (!exercise) {
    return null;
  }

  if (exercise.visibility === PrivacySettings.public) {
    return exercise;
  }

  if (exercise.visibility === PrivacySettings.private) {
    if (userId && exercise.author.toString() === userId.toString()) {
      return exercise;
    }
    return 'forbidden';
  }

  return null;
};

export const getExercisesService = async (
  filters: ExerciseFilters,
  sortBy: 'popular' | 'top-rated' | 'latest',
  page: number,
  pageSize: number,
  userId?: string,
): Promise<IExercise[]> => {
  const query: FilterQuery<IExercise> = {};

  if (userId) {
    query.$or = [{ visibility: PrivacySettings.public }, { author: userId }];
  } else {
    query.visibility = PrivacySettings.public;
  }

  // Fixed filter queries
  if (filters.muscleGroups) query.muscleGroup = { $in: filters.muscleGroups };
  if (filters.equipment) query.equipment = { $in: filters.equipment };
  if (filters.hashtags) query.hashtags = { $in: filters.hashtags };
  if (filters.difficulty) query.advancedLevel = filters.difficulty;

  if (filters.followers && userId) {
    const user = await User.findById(userId).select('followedUsers').lean();
    if (user) {
      query.author = { $in: user.followedUsers };
    }
  }

  if (filters.favorites && userId) {
    const user = await User.findById(userId).select('favorites.exercises').lean();
    if (user) {
      query._id = { $in: user.favorites.exercises };
    }
  }

  type SortType = Partial<Record<keyof IExercise, 1 | -1>>;
  let sort: SortType = {};

  if (sortBy === 'latest') {
    sort = { createdAt: -1 };
  } else if (sortBy === 'popular') {
    const since = new Date();
    since.setDate(since.getDate() - 1);
    query.createdAt = { $gte: since };
    sort = { likes: -1 };
  } else if (sortBy === 'top-rated') {
    sort = { likes: -1 };
  }

  const skip = (page - 1) * pageSize;

  return Exercise.find(query).sort(sort).skip(skip).limit(pageSize);
};

export const getExercisesByUserService = async (
  targetUserId: string,
  currentUserId?: string,
): Promise<IExercise[]> => {
  const query: FilterQuery<IExercise> = { author: targetUserId };

  if (!currentUserId || currentUserId !== targetUserId) {
    query.visibility = PrivacySettings.public;
  }

  return Exercise.find(query).sort({ createdAt: -1 });
};

export const getFavoriteExercisesService = async (userId: string): Promise<IExercise[]> => {
  const user = await User.findById(userId).select('favorites.exercises').lean();

  if (!user || !user.favorites || !user.favorites.exercises?.length) {
    return [];
  }

  return Exercise.find({ _id: { $in: user.favorites.exercises } });
};