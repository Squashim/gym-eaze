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
  downloadedHashtags,
} from "./test/downloadData";
import { seedHashtags } from "./data/hashtagsData";

const seed = async () => {
  await connectToDB();
  try {
    //await seedBadges();
    //await seedEquipment();
    //await seedMuscleGroups();
    //await seedHashtags();
    //await seedExercises();

    //await downloadedBadge();
    //await downloadedEquipment();
    //await downloadedMuscleGroup();
    //await downloadedHashtags();

    await downloadedExercises();
  } catch (error) {
    console.error("Błąd:", error);
  } finally {
    mongoose.disconnect();
  }
};

seed();
