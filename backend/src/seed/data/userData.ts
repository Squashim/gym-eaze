import { Types } from 'mongoose';
import User from '../../models/userModel';
import { UserRole } from '../../constants/roles';
import { Goal } from '../../constants/goals';
import { Gender } from '../../constants/genders';
import { StatusTraining } from '../../constants/statuses';
import bcrypt from 'bcrypt';

const saltRounds = 10;

interface User {
  password: string;
}

async function hashUsersPasswords(users: User[]) {
  for (const user of users) {
    user.password = await bcrypt.hash(user.password, saltRounds);
  }
}

export const seedUsers = async () => {
  await User.deleteMany();
  await hashUsersPasswords(users);
  await User.insertMany(users);
  console.log('Użytkownicy dodani');
};

const users = [
  {
    _id: new Types.ObjectId('68875a0b053920a2eca69cbe'), // monikaGym
    refreshToken: 'someRefreshToken',
    username: 'monikaGym',
    email: 'monikaGym@example.com',
    password: 'TestPasswordddd123!',
    lastLoginAt: new Date(),
    role: UserRole.user,
    favorites: {
      trainingPlans: [],
      exercises: [
        new Types.ObjectId('68875b48a6d40cbbe47d1d9a'),
        new Types.ObjectId('68875afe9d9533738f442263'),
      ],
    },
    followedUsers: [],
    followers: [
      new Types.ObjectId('6887cc687882c42182cdf1f5'), //paulaRunner
    ],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [
        {
          trainingPlanId: '6888e4ecdbbdf0460db19682',
          status: StatusTraining.active,
        },
      ],
    },
    profileDetails: {
      profilePicture: 'https://ibb.co/5XNdQ2n3',
      description: 'Miłośniczka fitnessu i zdrowego stylu życia.',
      goal: Goal.calorieBurn,
    },
    personalDetails: {
      firstName: 'Monika',
      lastName: 'Młynarczyk',
      country: 'PL',
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
        badge: new Types.ObjectId('6887b71977991eea1a365c96'),
        isPinnded: true,
        awardedAt: new Date(),
      },
      {
        badge: new Types.ObjectId('6887b73a2078947c7f327f80'),
        isPinned: false,
        awardedAt: new Date(),
      },
      {
        badge: new Types.ObjectId('6887b63aa3234565c7b5392b'),
        isPinned: true,
        awardedAt: new Date(),
      },
    ],
    notifications: [
      new Types.ObjectId('6887b9757bd7aefa4ba5edec'),
      new Types.ObjectId('6888cb96cc1c5787cec0f18f'),
      new Types.ObjectId('6888e5ab11463715323aa72c'),
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: new Types.ObjectId('6887c5f43118c221f43b4e23'), // fitnessFan
    username: 'fitnessFan',
    email: 'fitnessFan@example.com',
    password: 'TestPassword123',
    lastLoginAt: new Date(),
    role: UserRole.user,
    favorites: {
      trainingPlans: [],
      exercises: [new Types.ObjectId('6887c6be1f99e50d61923bb2')],
    },
    followedUsers: [],
    followers: [
      new Types.ObjectId('6887cc687882c42182cdf1f5'), // monikaGym
    ],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [],
    },
    profileDetails: {
      profilePicture: 'https://ibb.co/Z1t67gdW',
      description: 'Entuzjasta zdrowego stylu życia i aktywności fizycznej.',
      goal: Goal.muscleGain,
    },
    personalDetails: {
      firstName: 'Fabian',
      lastName: 'Czarnecki',
      country: 'PL',
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
        badge: new Types.ObjectId('6887b71977991eea1a365c96'),
        isPinned: true,
        awardedAt: new Date(),
      },
      {
        badge: new Types.ObjectId('6887b73a2078947c7f327f80'),
        isPinned: false,
        awardedAt: new Date(),
      },
    ],
    notifications: [
      new Types.ObjectId('6887b9757bd7aefa4ba5edec'),
      new Types.ObjectId('6888cb96cc1c5787cec0f18f'),
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: new Types.ObjectId('6887cc687882c42182cdf1f5'), // paulaRunner
    username: 'paulaRunner',
    email: 'paulaRunner@example.com',
    password: 'TestPassword123',
    lastLoginAt: new Date(),
    role: UserRole.user,
    favorites: {
      trainingPlans: [],
      exercises: [],
    },
    followedUsers: [
      new Types.ObjectId('68875a0b053920a2eca69cbe'), //MonikaGym
      new Types.ObjectId('6887c5f43118c221f43b4e23'), //fitnessFan
    ],
    followers: [],
    statistics: {
      totalWeight: 0,
      totalTrainingsTime: 0,
      startedTrainingPlans: [],
    },
    profileDetails: {
      profilePicture: 'https://ibb.co/TB3cbNCB',
      description: 'Biegaczka z pasją do długich dystansów.',
      goal: Goal.muscleGain,
    },
    personalDetails: {
      firstName: 'Paulina',
      lastName: 'Majewska',
      country: 'PL',
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
    badges: [
      {
        badge: new Types.ObjectId('6887b73a2078947c7f327f80'),
        isPinned: false,
        awardedAt: new Date(),
      },
    ],
    notifications: [new Types.ObjectId('6888cb96cc1c5787cec0f18f')],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
