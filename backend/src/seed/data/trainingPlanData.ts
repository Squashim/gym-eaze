import { AdvancedLevel } from "../../constants/advancedLevel";
import { Goal } from "../../constants/goals";
import { PrivacySettings } from "../../constants/privacySettings";
import TrainingPlan from "../../models/trainingPlanModel";

export const seedTrainingPlans = async () => {
  await TrainingPlan.deleteMany();
  await TrainingPlan.insertMany(trainingPlans);
  console.log("Plany treningowe dodane");
};

const trainingPlans = [
  {
    _id: "6888e00e3086d991b4fdcd80",
    author: "68875a0b053920a2eca69cbe", //Monika
    title: "Plan na masę",
    description: "Plan treningowy na masę mięśniową, 2 dni w tygodniu",
    image: "https://ibb.co/HLk4MrZj",
    goal: Goal.muscleGain,
    visibility: PrivacySettings.public,
    advancedLevel: AdvancedLevel.easy,
    likes: 2,
    days: [
      {
        day: {
          _id: "6888e0ea984bad83690eae99",
          name: "Poniedziałek",
          restBetweenExercises: 60,
          totalDuration: 800,
          isRestDay: false,
          exercises: [
            {
              exercise: {
                id: "68875afe9d9533738f442263",
                duration: 160,
                sets: [
                  { repetitions: 8, weight: 80, restAfterSet: 80 },
                  { repetitions: 8, weight: 80, restAfterSet: 80 },
                ],
              },
            },
            {
              exercise: {
                id: "68875b48a6d40cbbe47d1d9a",
                duration: 220,
                sets: [
                  { repetitions: 6, weight: 100, restAfterSet: 120 },
                  { repetitions: 6, weight: 100, restAfterSet: 120 },
                ],
              },
            },
          ],
        },
      },
      {
        day: {
          _id: "6888e104bdc36c2d039c9b8e",
          name: "Czwartek",
          restBetweenExercises: 60,
          totalDuration: 640,
          isRestDay: false,
          exercises: [
            {
              exercise: {
                id: "6887c69ea560a11573963786",
                duration: 320,
                sets: [
                  { repetitions: 8, weight: 70, restAfterSet: 90 },
                  { repetitions: 8, weight: 70, restAfterSet: 90 },
                ],
              },
            },
            {
              exercise: {
                id: "6887c6be1f99e50d61923bb2",
                duration: 320,
                sets: [
                  { repetitions: 6, weight: 0, restAfterSet: 120 },
                  { repetitions: 6, weight: 0, restAfterSet: 120 },
                ],
              },
            },
          ],
        },
      },
    ],
    hashtags: ["masa", "trening", "siła", "mięśnie"],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
