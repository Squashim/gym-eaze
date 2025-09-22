import { uploadTrainingPlanMedia } from '../config/cloudinary';
import TrainingPlan from '../models/trainingPlanModel';
import { CreateTrainingPlanData } from '../types/trainingPlan';
import {
  calculateExerciseDuration,
  validateTrainingPlanData,
  validateTrainingPlanFile,
} from '../utils/trainingPlanValidation';

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
