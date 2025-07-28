import { connectToDB } from "../config/db";
import { seedBadges } from "./data/badgeData";
import mongoose from "mongoose";
import { seedEquipment } from "./data/equipmentData";
import { seedMuscleGroups } from "./data/muscleGroupData";
import { seedExercises } from "./data/exerciseData";
import {
  downloadedExercises,
  downloadedMuscleGroup,
  downloadedBadge,
  downloadedEquipment,
  downloadedUsers,
} from "./test/downloadData";
import { seedUsers } from "./data/userData";

const seed = async () => {
  await connectToDB();
  try {
    //await seedBadges();
    //await seedEquipment();
    //await seedMuscleGroups();
    //await seedExercises();
    //await seedUsers();

    //await downloadedBadge();
    //await downloadedEquipment();
    //await downloadedMuscleGroup();
    //await downloadedExercises();
    await downloadedUsers();
  } catch (error) {
    console.error("Błąd:", error);
  } finally {
    mongoose.disconnect();
  }
};

seed();
