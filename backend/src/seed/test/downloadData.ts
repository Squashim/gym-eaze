import Badge from "../../models/badgeModel";
import Equipment from "../../models/equipmentModel";
import Exercise from "../../models/exerciseModel";
import MuscleGroup from "../../models/muscleGroup";
import User from "../../models/userModel";

export const downloadedBadge = async () => {
  const badge = await Badge.find();
  console.log(badge);
};

export const downloadedEquipment = async () => {
  const equipment = await Equipment.find();
  console.log(equipment);
};

export const downloadedMuscleGroup = async () => {
  const muscleGroups = await MuscleGroup.find();
  console.log(muscleGroups);
};

export const downloadedExercises = async () => {
  const exercises = await Exercise.find()
    .populate("muscleGroup")
    .populate("equipment")
    .populate("hashtags");

  console.log(JSON.stringify(exercises, null, 2));
};

export const downloadedUsers = async () => {
  const users = await User.find().populate("favorites.exercises.exerciseId");
  console.log(JSON.stringify(users, null, 2));
};
