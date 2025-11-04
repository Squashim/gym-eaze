import User from '../../models/userModel';
import { UserRole } from '../../constants/roles';
import { Goal } from '../../constants/goals';
import { ObjectId } from 'mongodb';
import bcrypt from 'bcrypt';
import { badgesIds } from './badgeData';
import { Country } from '../../constants/countries';
import { Gender } from '../../constants/genders';

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

export const userIds = {
  admin: new ObjectId('68aadfbccc15cf46f25867ad'), // admin
  monikaGym: new ObjectId('68d0243f1ef234cc5446b600'), // monikaGym
  walterWhite: new ObjectId('68dbb721ebd73dc55aa3be3a'), // WalterWhite
  jessiePinkman: new ObjectId('68dbb887cec66d85594d666f'), // JessiePinkman
  saulGoodman: new ObjectId('68dbba2ccf4ebefe086f0258'), // SaulGoodman
  hankSchrader: new ObjectId('68dbbad7fcab453d566dfc3f'), // HankSchrader
  gusFring: new ObjectId('68dbbb7acd76dd1143530531'), // GusFring
};

const users = [
  {
    _id: userIds.admin,
    username: 'GymEaze',
    email: 'gymeaze.admin@gmail.com',
    password: 'Admin12345678!',
    role: UserRole.admin,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762268402/exercises/images/slifczyhytpf0vvn9kx8.png',
      description: 'Oficjalne konto GymEaze',
      goal: Goal.calorieBurn,
    },
    followers: [userIds.monikaGym],
    followedUsers: [
      userIds.monikaGym,
      userIds.walterWhite,
      userIds.jessiePinkman,
      userIds.saulGoodman,
      userIds.hankSchrader,
      userIds.gusFring,
    ],
    privacySettings: {
      isTrainingStatisticsPublic: false,
    },
    badges: [
      {
        badge: badgesIds.time.oneYear,
        isPinnded: true,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.exercises.first,
        isPinned: false,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.exercises.five,
        isPinned: true,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.exercises.ten,
        isPinned: false,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.exercises.twenty,
        isPinned: true,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.workouts.first,
        isPinned: false,
        awardedAt: new Date(),
      },
      {
        badge: badgesIds.workouts.five,
        isPinned: false,
        awardedAt: new Date(),
      },
    ],
  },
  {
    _id: userIds.monikaGym,
    username: 'monikaGym',
    email: 'monikaGym@example.com',
    password: 'monikaGym12345678!',
    role: UserRole.user,
    profileDetails: {
      description: 'Miłośniczka fitnessu i zdrowego stylu życia.',
      goal: Goal.muscleGain,
    },
    personalDetails: {
      firstName: 'Monika',
      country: Country.PL,
    },
    fitnessDetails: {
      height: 160,
      weight: 62,
      age: 28,
      gender: Gender.female,
    },
    followers: [userIds.admin],
  },
  {
    _id: userIds.walterWhite,
    username: 'WalterWhite',
    email: 'walterwhite@example.com',
    password: 'WalterWhite123!',
    role: UserRole.user,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759230010/walterwhite_xfninq.png',
      description: 'Ja jestem tym, który ćwiczy.',
      goal: Goal.endurance,
    },
    personalDetails: {
      firstName: 'Walter',
      lastName: 'White',
      country: Country.US,
    },
    fitnessDetails: {
      height: 175,
      weight: 78,
      age: 52,
      gender: Gender.male,
    },
    privacySettings: {
      isPersonalDetailsPublic: false,
      isFitnessDetailsPublic: true,
      isBadgesDetailsPublic: true,
      isGoalPublic: true,
      isTrainingStatisticsPublic: false,
    },
    followers: [userIds.admin],
  },
  {
    _id: userIds.jessiePinkman,
    username: 'JessiePinkman',
    email: 'jessiepinkman@example.com',
    password: 'JessiePinkman123!',
    role: UserRole.user,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759230219/jessiePinkman_r3kx75.png',
    },
    fitnessDetails: {
      height: 173,
      weight: 70,
      age: 25,
      gender: Gender.male,
    },
    followers: [userIds.admin],
  },
  {
    _id: userIds.saulGoodman,
    username: 'SaulGoodman',
    email: 'saulgoodman@example.com',
    password: 'SaulGoodman123!',
    role: UserRole.user,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759230476/Saul_Goodman_xyplfj.png',
      description: 'Lepiej zadzwoń do Saula!',
      goal: Goal.flexibility,
    },
    personalDetails: {
      firstName: 'Jimmy',
      country: Country.US,
    },
    fitnessDetails: {
      height: 177,
      weight: 85,
      age: 47,
      gender: Gender.male,
    },
    followers: [userIds.admin],
  },
  {
    _id: userIds.hankSchrader,
    username: 'HankSchrader',
    email: 'hankschrader@example.com',
    password: 'HankSchrader123!',
    role: UserRole.user,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759230643/Hank_Schrader_wz7mov.png',
      description: 'Kolekcjoner odznak i minerałów.',
      goal: Goal.strength,
    },
    personalDetails: {
      firstName: 'Hank',
      country: Country.US,
    },
    fitnessDetails: {
      height: 183,
      weight: 95,
      age: 45,
      gender: Gender.male,
    },
    followers: [userIds.admin],
  },
  {
    _id: userIds.gusFring,
    username: 'GusFring',
    email: 'gusfring@example.com',
    password: 'GusFring12345!',
    role: UserRole.user,
    profileDetails: {
      profilePicture:
        'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759230808/Gus_Fring_q3a5nt.png',
    },
    personalDetails: {
      firstName: 'Gustavo',
      lastName: 'Fring',
      country: Country.CL,
    },
    privacySettings: {
      isPersonalDetailsPublic: false,
      isFitnessDetailsPublic: false,
      isBadgesDetailsPublic: false,
      isGoalPublic: false,
      isTrainingStatisticsPublic: false,
    },
    fitnessDetails: {
      height: 180,
      weight: 82,
      age: 55,
      gender: Gender.male,
    },
    followers: [userIds.admin],
  },
];
