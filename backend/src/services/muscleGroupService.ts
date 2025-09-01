import MuscleGroup, { IMuscleGroup } from "../models/muscleGroup";


export const getAllMuscleGroups = async (): Promise<IMuscleGroup[]> => {
  return await MuscleGroup.find();
};

export const getMuscleGroupById = async (id: string): Promise<IMuscleGroup | null> => {
  return await MuscleGroup.findById(id);
};