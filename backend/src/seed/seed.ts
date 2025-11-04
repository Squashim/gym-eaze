import { connectToDB } from '../config/db';
import { seedBadges } from './data/badgeData';
import mongoose from 'mongoose';
import { seedEquipment } from './data/equipmentData';
import { seedMuscleGroups } from './data/muscleGroupData';
import { seedExercises } from './data/exerciseData';
import {
  downloadedExercises,
  downloadedMuscleGroup,
  downloadedBadge,
  downloadedEquipment,
  downloadedUsers,
  downloadedPosts,
  downloadedTrainingPlans,
  downloadedComments,
} from './test/downloadData';
import { seedUsers } from './data/userData';
import { seedPosts } from './data/postData';
import { seedTrainingPlans } from './data/trainingPlanData';
import { seedComments } from './data/commentData';

const checkAddedData = async () => {
  await connectToDB();
  try {
    await downloadedExercises();
    await downloadedMuscleGroup();
    await downloadedBadge();
    await downloadedEquipment();
    await downloadedUsers();
    await downloadedPosts();
    await downloadedTrainingPlans();
    await downloadedComments();
  } catch (error) {
    console.error('Błąd', error);
  }
};

const seed = async () => {
  await connectToDB();
  try {
    await seedBadges();
    await seedEquipment();
    await seedMuscleGroups();
    await seedUsers();
    await seedExercises();
    await seedPosts();
    await seedTrainingPlans();
    await seedComments();
  } catch (error) {
    console.error('Błąd:', error);
  } finally {
    mongoose.disconnect();
  }
};

const run = async () => {
  // await checkAddedData();
  await seed();
};

run();
