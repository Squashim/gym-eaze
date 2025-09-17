import { uploadTrainingPlanMedia } from '../config/cloudinary';
import TrainingPlan from '../models/trainingPlanModel';
import { CreateTrainingPlanData } from '../types/trainingPlan';
import {
  calculateDayDuration,
  validateTrainingPlanData,
  validateTrainingPlanFile,
} from '../utils/trainingPlanValidation';

export const createTrainingPlan = async (data: CreateTrainingPlanData, imageBuffer: Buffer) => {
  validateTrainingPlanData(data);
  validateTrainingPlanFile(imageBuffer);

  const { imageUrl } = await uploadTrainingPlanMedia(imageBuffer);

  const daysWithDuration = data.days.map((day) => {
    const totalDuration = calculateDayDuration(day);

    return {
      ...day,
      totalDuration,
    };
  });

  const trainingPlan = new TrainingPlan({
    name: data.title,
    author: data.author,
    description: data.description,
    goal: data.goal,
    visibility: data.visibility,
    advancedLevel: data.advancedLevel,
    hashtags: data.hashtags,
    image: imageUrl,
    days: daysWithDuration,
  });

  return await trainingPlan.save();
};
