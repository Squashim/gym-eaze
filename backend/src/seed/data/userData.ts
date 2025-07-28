import { Types } from "mongoose";
import User from "../../models/userModel";
import { UserRole } from "../../constants/roles";
import { Goal } from "../../constants/goals";
import { Gender } from "../../constants/genders";

export const seedUsers = async () => {
  await User.deleteMany();
  await User.insertMany(users);
  console.log("Użytkownicy dodani");
};

const users = [
  {
    _id: new Types.ObjectId("68875a0b053920a2eca69cbe"),
    username: "monikaGym",
    email: "monikaGym@example.com",
    password: "TestPassword123",
    createdAccountAt: new Date(),
    lastLoginAt: new Date(),
    role: UserRole.User,
    favorites: {
      trainingPlans: [],
      exercises: [
        new Types.ObjectId("68875b48a6d40cbbe47d1d9a"),

        new Types.ObjectId("68875afe9d9533738f442263"),
        //po prostutablic i tyle
      ],
    },
    followedUsers: [],
    followers: [
      //tak samo tablica
      {
        followerId: new Types.ObjectId("6887cc687882c42182cdf1f5"), //paulaRunner
        addedAt: new Date(),
      },
    ],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [],
    },
    exercises: [
      //tak samo
      {
        exerciseId: new Types.ObjectId("68875afe9d9533738f442263"),
      },
      {
        exerciseId: new Types.ObjectId("68875b48a6d40cbbe47d1d9a"),
      },
    ],
    trainingPlans: [],
    posts: [
      //tak samo
      {
        postId: new Types.ObjectId("6887ce64297974f8c21a1fcf"),
      },
    ],
    profileDetails: {
      profilePicture: "https://ibb.co/5XNdQ2n3",
      description: "Miłośniczka fitnessu i zdrowego stylu życia.",
      goal: Goal.CalorieBurn,
    },
    personalDetails: {
      firstName: "Monika",
      lastName: "Młynarczyk",
      country: "PL",
    },
    fitnessDetails: {
      height: 165,
      weight: 60,
      age: 28,
      gender: Gender.female,
    },
    privacySettings: {
      isPersonalDetailsPublic: true,
      isFitnessDetailsPublic: true,
      isBadgesDetailsPublic: true,
      isGoalPublic: true,
      isTrainingStatisticsPublic: true,
    },
    badges: [
      {
        //PAMIETAJ ABY USUWAC _id
        badge: new Types.ObjectId("6887b71977991eea1a365c96"),
        isPinnded: true,
        awardedAt: new Date(),
        _id: false,
      },
    ],
    notifications: [
      //sama talica
      {
        notificationId: new Types.ObjectId("6887b9757bd7aefa4ba5edec"),
      },
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: new Types.ObjectId("6887c5f43118c221f43b4e23"),
    username: "fitnessFan",
    email: "fitnessFan@example.com",
    password: "TestPassword123",
    createdAccountAt: new Date(),
    lastLoginAt: new Date(),
    role: UserRole.User,
    favorites: {
      trainingPlans: [],
      exercises: [
        {
          exerciseId: new Types.ObjectId("6887c6be1f99e50d61923bb2"),
          addedAt: new Date(),
        },
      ],
    },
    followedUsers: [],
    followers: [],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [],
    },
    exercises: [
      {
        exerciseId: new Types.ObjectId("68875afe9d9533738f442263"), //1 cwiczenie
      },
      {
        exerciseId: new Types.ObjectId("68875b48a6d40cbbe47d1d9a"), //2 cwiczenie
      },
    ],
    trainingPlans: [],
    posts: [],
    profileDetails: {
      profilePicture: "https://ibb.co/Z1t67gdW",
      description: "Entuzjasta zdrowego stylu życia i aktywności fizycznej.",
      goal: Goal.MuscleGain,
    },
    personalDetails: {
      firstName: "Fabian",
      lastName: "Czarnecki",
      country: "PL",
    },
    fitnessDetails: {
      height: 195,
      weight: 90,
      age: 32,
      gender: Gender.male,
    },
    privacySettings: {
      isPersonalDetailsPublic: true,
      isFitnessDetailsPublic: true,
      isBadgesDetailsPublic: true,
      isGoalPublic: true,
      isTrainingStatisticsPublic: true,
    },
    badges: [
      {
        badge: new Types.ObjectId("6887b71977991eea1a365c96"),
        isPinnded: true,
        awardedAt: new Date(),
      },
    ],
    notifications: [
      {
        notificationId: new Types.ObjectId("6887b9757bd7aefa4ba5edec"),
      },
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: new Types.ObjectId("6887cc687882c42182cdf1f5"),
    username: "paulaRunner",
    email: "paulaRunner@example.com",
    password: "TestPassword123",
    createdAccountAt: new Date(),
    lastLoginAt: new Date(),
    role: UserRole.User,
    favorites: {
      trainingPlans: [],
      exercises: [],
    },
    followedUsers: [
      {
        followedUserId: new Types.ObjectId("68875a0b053920a2eca69cbe"), //MonikaGym
        addedAt: new Date(),
      },
    ],
    followers: [],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [],
    },
    exercises: [],
    trainingPlans: [],
    posts: [],
    profileDetails: {
      profilePicture: "https://ibb.co/TB3cbNCB",
      description: "Biegaczka z pasją do długich dystansów.",
      goal: Goal.MuscleGain,
    },
    personalDetails: {
      firstName: "Paulina",
      lastName: "Majewska",
      country: "PL",
    },
    fitnessDetails: {
      height: 173,
      weight: 60,
      age: 22,
      gender: Gender.female,
    },
    privacySettings: {
      isPersonalDetailsPublic: false,
      isFitnessDetailsPublic: false,
      isBadgesDetailsPublic: true,
      isGoalPublic: true,
      isTrainingStatisticsPublic: true,
    },
    badges: [],
    notifications: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
