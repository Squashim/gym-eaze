import UserHasTrainingPlans from "../../models/userHasTrainingPlansModel";
export const seedUserHasTrainingPlans = async () => {
  await UserHasTrainingPlans.deleteMany();
  await UserHasTrainingPlans.insertMany(userHasTrainingPlans);
  console.log(
    "Użytkownicy z rozpoczetymi/ukończonymi/anulowanymi planami treningowymi dodani",
  );
};

const userHasTrainingPlans = [
  {
    _id: "6888e4ecdbbdf0460db19682",
    trainingPlanId: "6888e00e3086d991b4fdcd80",
    progress: {
      totalDays: 2,
      completedDays: {
        day: "6888e0ea984bad83690eae99",
        exercises: [
          {
            id: "68875afe9d9533738f442263",
            sets: [
              { balancePercentage: 50, tempoPercentage: 70, time: 30 },
              { balancePercentage: 60, tempoPercentage: 80, time: 40 },
            ],
          },
          {
            id: "68875b48a6d40cbbe47d1d9a",
            sets: [
              { balancePercentage: 55, tempoPercentage: 75, time: 35 },
              { balancePercentage: 65, tempoPercentage: 85, time: 45 },
            ],
          },
        ],
      },
    },
  },
];
