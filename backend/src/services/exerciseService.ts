import Exercise from "../models/exerciseModel";
import { uploadExerciseMedia } from "../config/cloudinary"
import { validateExerciseData, ExerciseValidationData } from "../utils/exerciseValidation";

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