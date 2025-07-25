import Badge from "../../models/badgeModel";
import Equipment from "../../models/equipmentModel";
import Exercise from "../../models/exerciseModel";
import MuscleGroup from "../../models/muscleGroup";
import Hashtag from "../../models/hashtagsModel";
//robie zmiany

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

export const downloadedHashtags = async () => {
  const hashtags = await Hashtag.find();
  console.log(hashtags);
};

export const downloadedExercises = async () => {
  const exercises = await Exercise.find()
    .populate("muscleGroup")
    .populate("equipment")
    .populate("hashtags");

  console.log(exercises);
};
