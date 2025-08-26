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
  downloadedUserHasTrainingPlans,
} from './test/downloadData';
import { seedUsers } from './data/userData';
import { seedPosts } from './data/postData';
import { seedTrainingPlans } from './data/trainingPlanData';
import { seedComments } from './data/commentData';
import { seedUserHasTrainingPlans } from './data/userHasTrainingPlansData';
import { seedNotifications } from './data/notificationData';

const checkAddedData = async () => {
  await connectToDB();
  try {
    //await downloadedExercises();
    //await downloadedMuscleGroup();
    //await downloadedBadge();
    //await downloadedEquipment();
    await downloadedUsers();
    //await downloadedPosts();
    //await downloadedTrainingPlans();
    //await downloadedComments();
    //await downloadedUserHasTrainingPlans();
  } catch (error) {
    console.error('Błąd', error);
  }
};

const seed = async () => {
  await connectToDB();
  try {
    //await seedBadges();
    //await seedEquipment();
    //await seedMuscleGroups();
    //await seedExercises();
    //await seedUsers();
    //await seedPosts();
    //await seedTrainingPlans();
    //await seedComments();
    //await seedUserHasTrainingPlans();
    //await seedNotifications();
  } catch (error) {
    console.error('Błąd:', error);
  } finally {
    mongoose.disconnect();
  }
};

const run = async () => {
  await checkAddedData(); //Sprawdzenie czy dane zostały dodane
  //await seed(); //Dodawanie danych do bazy
};

run();
