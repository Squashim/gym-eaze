import Badge from '../../models/badgeModel';
import Equipment from '../../models/equipmentModel';
import Exercise from '../../models/exerciseModel';
import MuscleGroup from '../../models/muscleGroup';
import Post from '../../models/postModel';
import TrainingPlan from '../../models/trainingPlanModel';
import User from '../../models/userModel';
import Comment from '../../models/commentModel';
import UserHasTrainingPlans from '../../models/startedTrainingPlanModel';

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
    .populate('muscleGroup')
    .populate('equipment')
    .populate('hashtags');

  console.log(JSON.stringify(exercises, null, 2));
};

export const downloadedUsers = async () => {
  const users = await User.find().populate('favorites.exercises.exerciseId');
  console.log(JSON.stringify(users, null, 2));
};

export const downloadedPosts = async () => {
  const posts = await Post.find().populate('author');
  console.log(JSON.stringify(posts, null, 2));
};

export const downloadedTrainingPlans = async () => {
  const trainingPlans = await TrainingPlan.find();
  console.log(JSON.stringify(trainingPlans, null, 2));
};

export const downloadedComments = async () => {
  const comments = await Comment.find();
  console.log(JSON.stringify(comments, null, 2));
};

export const downloadedUserHasTrainingPlans = async () => {
  const userHasTrainingPlans = await UserHasTrainingPlans.find();
  console.log(JSON.stringify(userHasTrainingPlans, null, 2));
};
