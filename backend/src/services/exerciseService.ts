import Exercise, { IExercise } from "../models/exerciseModel";
import { uploadExerciseMedia } from "../config/cloudinary"
import { validateExerciseData, validateExercisePatchData } from "../utils/exerciseValidation";
import mongoose, { Types } from "mongoose";
import User from "../models/userModel";

interface ExerciseData {
  author: string;
  name: string;
  description: string;
  visibility: string;
  advancedLevel: string;
  muscleGroup: string[];
  equipment?: string[];
  hashtags?: string[];
}

export const createExercise = async (
  data: ExerciseData, 
  imageBuffer?: Buffer, 
  videoBuffer?: Buffer
) => {
  validateExerciseData(data);

  const { imageUrl, videoUrl } = await uploadExerciseMedia(imageBuffer, videoBuffer);


  const trimmedName = data.name.trim();
  const trimmedDescription = data.description.trim();

  const exercise = new Exercise({
    ...data,
    name: trimmedName,
    description: trimmedDescription,
    muscleGroup: (data.muscleGroup || []).map(id => ({ type: id })), 
    equipment: (data.equipment || []).map(id => ({ type: id })),     
    image: imageUrl,
    video: videoUrl,
    likes: 0,
  });

  return await exercise.save();
};

export const deleteExercise = async (exerciseId: string, userId: string) => {
  const exercise = await Exercise.findById(exerciseId);

  if (!exercise) {
    throw new Error("NOT_FOUND");
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
  data: Partial<ExerciseData>,
  imageBuffer?: Buffer,
  videoBuffer?: Buffer
) => {
  const exercise = await Exercise.findById(exerciseId);

  if (!exercise) {
    throw new Error("NOT_FOUND");
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

  Object.assign(exercise, {
    name: data.name?.trim() || exercise.name,
    description: data.description?.trim() || exercise.description,
    visibility: data.visibility || exercise.visibility,
    advancedLevel: data.advancedLevel || exercise.advancedLevel,
    muscleGroup: data.muscleGroup ? data.muscleGroup.map(id => ({ type: id })) : exercise.muscleGroup,
    equipment: data.equipment ? data.equipment.map(id => ({ type: id })) : exercise.equipment,
    hashtags: data.hashtags || exercise.hashtags,
    image: imageUrl,
    video: videoUrl,
    updatedAt: new Date()
  });

  return await exercise.save();
};

export const toggleExerciseLikeService = async (
  userId: string,
  exerciseId: string
) => {
  if (!mongoose.Types.ObjectId.isValid(exerciseId)) {
    throw new Error("Nieprawidłowe ID ćwiczenia");
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }

  const exercise = await Exercise.findById(exerciseId);
  if (!exercise) {
    throw new Error("Nie znaleziono ćwiczenia");
  }

  const isLiked = user.likes.exercises.some(fav => fav.toString() === exerciseId);

if (isLiked) {
  user.likes.exercises = user.likes.exercises.filter(fav => fav.toString() !== exerciseId);
  exercise.likes = Math.max(0, (exercise.likes || 0) - 1);
  await user.save();
  await exercise.save();
  return { message: "Usunięto polubienie", likes: exercise.likes };
} else {
  user.likes.exercises.push(exercise._id as Types.ObjectId);
  exercise.likes = (exercise.likes || 0) + 1;
  await user.save();
  await exercise.save();
  return { message: "Dodano polubienie", likes: exercise.likes };
}
};

export const toggleExerciseFavoriteService = async (userId: string, exerciseId: string) => {
  if (!mongoose.Types.ObjectId.isValid(exerciseId)) {
    throw new Error("Nieprawidłowe ID ćwiczenia");
  }

  const exercise = await Exercise.findById(exerciseId);
  if (!exercise) {
    throw new Error("Ćwiczenie nie istnieje");
  }

  if (exercise.author.toString() === userId.toString()) {
    throw new Error("Nie możesz dodać własnego ćwiczenia do ulubionych");
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }

  const isFavorite = user.favorites.exercises.some(
    fav => fav.toString() === exerciseId.toString()
  );

  if (isFavorite) {
    user.favorites.exercises = user.favorites.exercises.filter(
      fav => fav.toString() !== exerciseId.toString()
    );
    await user.save();
    return { message: "Usunięto ćwiczenie z ulubionych" };
  } else {
    user.favorites.exercises.push(exercise._id as mongoose.Types.ObjectId);
    await user.save();
    return { message: "Dodano ćwiczenie do ulubionych" };
  }
};

export const getExerciseByIdService = async (
  id: string,
  userId?: string
): Promise<typeof exercise | null | "forbidden"> => {
  const exercise = await Exercise.findById(id).lean();

  if (!exercise) {
    return null; 
  }

  if (exercise.visibility === "Publiczny") {
    return exercise; 
  }

  if (exercise.visibility === "Prywatny") {
    if (userId && exercise.author.toString() === userId.toString()) {
      return exercise; 
    }
    return "forbidden"; 
  }

  return null; 
};

export const getExercisesService = async (
  filters: any,
  sortBy: string,
  page: number,
  pageSize: number,
  userId?: string
): Promise<IExercise[]> => {
  const query: any = {};

  if (userId) {
    query.$or = [{ visibility: "Publiczny" }, { author: userId }];
  } else {
    query.visibility = "Publiczny";
  }

  if (filters.muscleGroups) query["muscleGroup.type"] = { $in: filters.muscleGroups };
  if (filters.equipment) query["equipment.type"] = { $in: filters.equipment };
  if (filters.hashtags) query.hashtags = { $in: filters.hashtags };
  if (filters.difficulty) query.advancedLevel = filters.difficulty;

  if (filters.followers && userId) {
    const user = await User.findById(userId).select("followedUsers").lean();
    if (user) {
      query.author = { $in: user.followedUsers };
    }
  }

  if (filters.favorites && userId) {
    const user = await User.findById(userId).select("favorites.exercises").lean();
    if (user) {
      query._id = { $in: user.favorites.exercises };
    }
  }

  let sort: any = { createdAt: -1 };
  if (sortBy === "popular") {
    const since = new Date();
    since.setDate(since.getDate() - 1); 
    query.createdAt = { $gte: since };
    sort = { likes: -1 };
  } else if (sortBy === "top-rated") {
    sort = { likes: -1 };
  }

  const skip = (page - 1) * pageSize;

  return Exercise.find(query).sort(sort).skip(skip).limit(pageSize);
};


export const getExercisesByUserService = async (
  targetUserId: string,
  currentUserId?: string
): Promise<IExercise[]> => {
  const query: any = { author: targetUserId };

  if (!currentUserId || currentUserId !== targetUserId) {
    query.visibility = "Publiczny";
  }

  return Exercise.find(query).sort({ createdAt: -1 });
};

export const getFavoriteExercisesService = async (userId: string): Promise<IExercise[]> => {
  const user = await User.findById(userId).select("favorites.exercises").lean();

  if (!user || !user.favorites || !user.favorites.exercises?.length) {
    return [];
  }

  return Exercise.find({ _id: { $in: user.favorites.exercises } });
};

