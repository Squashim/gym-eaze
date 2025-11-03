import { IBadge } from '../models/badgeModel';
import Badge from '../models/badgeModel';
import { deleteFromCloudinary, uploadExerciseMedia } from '../config/cloudinary';
import User, { IUser } from '../models/userModel';
import { Goal } from '../constants/goals';
import bcrypt from 'bcryptjs';
import { Gender } from '../constants/genders';
import { Country } from '../constants/countries';
import mongoose, { Types } from 'mongoose';
import { UserProfile } from '../types/user';
import DeviceToken from '../models/deviceTokenModel';
import { sendNotificationService } from './notificationService';
import { Notifications } from '../constants/notifications';
import { checkAndEarnBadges } from './badgeService';
import UserHasTrainingPlans from '../models/userHasTrainingPlansModel';
import Notification from '../models/notificationModel';
import Post from '../models/postModel';
import { deleteCommentsAndLikes } from '../utils/helpers';
import { CommentParentType } from '../constants/commentType';
import TrainingPlan from '../models/trainingPlanModel';
import Exercise from '../models/exerciseModel';
import Comment from '../models/commentModel';

export const getUserLikesService = async (userId: string) => {
  const user = await User.findById(userId).select('likes');
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }
  return user.likes;
};

export const getUserFavoritesService = async (userId: string) => {
  const user = await User.findById(userId).select('favorites');
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }
  return user.favorites;
};

export const getUserProfileDetailsService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: 'badges.badge',
    model: Badge,
  });

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const pinnedBadges =
    user.badges
      ?.filter((b) => b.isPinned && b.badge)
      .map((b) => ({
        id: (b.badge as IBadge)._id,
        title: (b.badge as IBadge).title || '',
        description: (b.badge as IBadge).description || '',
        image: (b.badge as IBadge).image || '',
      })) || [];

  return {
    profilePicture: user.profileDetails?.profilePicture || '',
    description: user.profileDetails?.description || '',
    goal: user.profileDetails?.goal || '',
    pinnedBadges,
    isBadgesDetailsPublic: user.privacySettings?.isBadgesDetailsPublic ?? true,
    isGoalPublic: user.privacySettings?.isGoalPublic ?? true,
  };
};

export const updateUserProfileDetailsService = async (
  userId: string,
  description: string,
  goal: string | undefined,
  pinnedBadges: string[] | undefined,
  file?: Express.Multer.File,
) => {
  const user = await User.findById(userId).populate('badges.badge');
  if (!user) {
    throw new Error('Użytkownik nie znaleziony');
  }

  let profilePicture = user.profileDetails?.profilePicture || '';
  if (file) {
    const uploaded = await uploadExerciseMedia(file.buffer, undefined);
    if (uploaded.imageUrl) {
      await deleteFromCloudinary(profilePicture);
      profilePicture = uploaded.imageUrl;
    }
  }

  if (Array.isArray(pinnedBadges)) {
    if (pinnedBadges.length > 5) {
      throw new Error('Możesz przypiąć maksymalnie 5 odznak');
    }

    (user.badges ?? []).forEach((b) => {
      const isPinned = pinnedBadges.includes(b.badge._id.toString());
      b.isPinned = isPinned;
    });
  }

  user.profileDetails = {
    ...user.profileDetails,
    description: description || user.profileDetails?.description || '',
    goal: (goal as Goal) ?? user.profileDetails?.goal,
    profilePicture,
  };

  await user.save();

  return { message: 'Profil zaktualizowany pomyślnie' };
};

export const getUserBadgesService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: 'badges.badge',
    model: Badge,
  });

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const allBadges =
    user.badges
      ?.filter((b) => b.badge)
      .map((b) => ({
        id: (b.badge as IBadge)._id,
        title: (b.badge as IBadge).title,
        description: (b.badge as IBadge).description,
        image: (b.badge as IBadge).image,
      })) || [];

  return allBadges;
};

interface UpdateUserInput {
  username?: string;
  password?: string;
}

export const updateUserService = async (userId: string, data: UpdateUserInput) => {
  const { username, password } = data;
  const updateData: Partial<IUser> = {};

  if (username) {
    if (username.length < 6 || username.length > 14) {
      throw new Error('USERNAME_LENGTH');
    }

    const existingUser = await User.findOne({ username });
    if (existingUser && existingUser._id.toString() !== userId) {
      throw new Error('USERNAME_EXISTS');
    }

    updateData.username = username;
  }

  if (password) {
    if (password.length < 14 || password.length > 32) {
      throw new Error('PASSWORD_LENGTH');
    }

    if (!/[A-Z]/.test(password)) {
      throw new Error('PASSWORD_UPPERCASE');
    }

    if (!/[a-z]/.test(password)) {
      throw new Error('PASSWORD_LOWERCASE');
    }

    if (!/\d/.test(password)) {
      throw new Error('PASSWORD_NUMBER');
    }

    if (!/[!@#$%^&*()_+./]/.test(password)) {
      throw new Error('PASSWORD_SPECIAL');
    }

    const existingUser = await User.findById(userId);
    if (existingUser && (await bcrypt.compare(password, existingUser.password))) {
      throw new Error('SAME_PASSWORD');
    }

    const hashedPassword = await bcrypt.hash(password, 12);
    updateData.password = hashedPassword;
  }

  const updatedUser = await User.findByIdAndUpdate(userId, updateData, { new: true });
  return updatedUser;
};

export const deleteUserService = async (userId: string) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  await DeviceToken.deleteMany({ userId });

  if (user.profileDetails?.profilePicture) {
    await deleteFromCloudinary(user.profileDetails.profilePicture);
  }

  await UserHasTrainingPlans.deleteMany({ userId: user._id });

  await Notification.deleteMany({ to: user._id });

  const userPosts = await Post.find({ author: user._id });
  for (const post of userPosts) {
    if (post.image) {
      await deleteFromCloudinary(post.image);
    }
    if (post.video) {
      await deleteFromCloudinary(post.video, 'video');
    }

    await deleteCommentsAndLikes(CommentParentType.post, (post._id as Types.ObjectId).toString());
  }
  await Post.deleteMany({ author: user._id });

  const userTrainingPlans = await TrainingPlan.find({ author: user._id });
  for (const plan of userTrainingPlans) {
    await deleteCommentsAndLikes(
      CommentParentType.training,
      (plan._id as Types.ObjectId).toString(),
    );
  }
  await TrainingPlan.deleteMany({ author: user._id });

  const userExercises = await Exercise.find({ author: user._id });
  for (const exercise of userExercises) {
    await deleteCommentsAndLikes(
      CommentParentType.exercise,
      (exercise._id as Types.ObjectId).toString(),
    );
  }
  await Exercise.deleteMany({ author: user._id });

  const userComments = await Comment.find({ author: userId });
  const commentIds = userComments.map((comment) => comment._id);
  if (commentIds.length > 0) {
    await User.updateMany(
      { 'likes.comments': { $in: commentIds } },
      { $pull: { 'likes.comments': { $in: commentIds } } },
    );
  }
  await Comment.deleteMany({ author: user._id });

  await User.updateMany(
    {
      followers: user._id,
    },
    {
      $pull: { followers: user._id },
    },
  );

  await User.updateMany(
    {
      followedUsers: user._id,
    },
    {
      $pull: { followedUsers: user._id },
    },
  );

  await User.updateMany(
    { 'likes.exercises': { $in: user.likes?.exercises || [] } },
    { $pull: { 'likes.exercises': { $in: user.likes?.exercises || [] } } },
  );

  await User.updateMany(
    { 'likes.posts': { $in: user.likes?.posts || [] } },
    { $pull: { 'likes.posts': { $in: user.likes?.posts || [] } } },
  );

  await User.updateMany(
    { 'likes.trainingPlans': { $in: user.likes?.trainingPlans || [] } },
    { $pull: { 'likes.trainingPlans': { $in: user.likes?.trainingPlans || [] } } },
  );

  await User.updateMany(
    { 'favorites.exercises': { $in: user.favorites?.exercises || [] } },
    { $pull: { 'favorites.exercises': { $in: user.favorites?.exercises || [] } } },
  );

  await User.updateMany(
    { 'favorites.trainingPlans': { $in: user.favorites?.trainingPlans || [] } },
    { $pull: { 'favorites.trainingPlans': { $in: user.favorites?.trainingPlans || [] } } },
  );

  const deletedUser = await user.deleteOne();

  return deletedUser;
};

export const getUserPersonalDataService = async (userId: string) => {
  const user = await User.findById(userId).select('personalDetails privacySettings');

  if (!user) {
    throw new Error('Użytkownik nie znaleziony');
  }

  return {
    firstName: user.personalDetails?.firstName || '',
    lastName: user.personalDetails?.lastName || '',
    country: user.personalDetails?.country || '',
    isPersonalDetailsPublic: user.privacySettings.isPersonalDetailsPublic,
  };
};

interface UpdatePersonalDataInput {
  firstName?: string;
  lastName?: string;
  country?: Country;
}

export const updateUserPersonalDataService = async (
  userId: string,
  data: UpdatePersonalDataInput,
): Promise<boolean> => {
  const nameRegex = /^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$/;

  const formatName = (value: string): string => {
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  };

  if (data.firstName) {
    if (data.firstName.length > 25) {
      throw new Error('Imię nie może być dłuższe niż 25 znaków');
    }
    if (!nameRegex.test(data.firstName)) {
      throw new Error('Imię może zawierać tylko litery');
    }
  }

  if (data.lastName) {
    if (data.lastName.length > 25) {
      throw new Error('Nazwisko nie może być dłuższe niż 25 znaków');
    }
    if (!nameRegex.test(data.lastName)) {
      throw new Error('Nazwisko może zawierać tylko litery');
    }
  }

  if (data.country && !Object.values(Country).includes(data.country)) {
    throw new Error('Niepoprawny kraj');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie znaleziony');
  }

  user.personalDetails = {
    firstName: data.firstName
      ? formatName(data.firstName)
      : (user.personalDetails?.firstName ?? ''),
    lastName: data.lastName ? formatName(data.lastName) : (user.personalDetails?.lastName ?? ''),
    country: data.country ?? user.personalDetails?.country ?? Country.NONE,
  };

  await user.save();

  return true;
};

export const getUserFitnessDataService = async (userId: string) => {
  const user = await User.findById(userId).select('fitnessDetails privacySettings');

  if (!user) {
    throw new Error('Użytkownik nie znaleziony');
  }

  return {
    age: user.fitnessDetails?.age ?? null,
    height: user.fitnessDetails?.height ?? null,
    weight: user.fitnessDetails?.weight ?? null,
    gender: user.fitnessDetails?.gender ?? null,
    isFitnessDetailsPublic: user.privacySettings.isFitnessDetailsPublic,
  };
};

interface UpdateFitnessDataInput {
  age?: number;
  height?: number;
  weight?: number;
  gender?: Gender;
}

export const updateUserFitnessDataService = async (
  userId: string,
  data: UpdateFitnessDataInput,
): Promise<boolean> => {
  const age = data.age !== undefined ? Number(data.age) : undefined;
  const height = data.height !== undefined ? Number(data.height) : undefined;
  const weight = data.weight !== undefined ? Number(data.weight) : undefined;
  const gender = data.gender;

  if (age !== undefined && (age < 0 || age > 120)) {
    throw new Error('Nieprawidłowy wiek');
  }

  if (height !== undefined && (height < 50 || height > 300)) {
    throw new Error('Nieprawidłowy wzrost');
  }

  if (weight !== undefined && (weight < 10 || weight > 500)) {
    throw new Error('Nieprawidłowa waga');
  }

  if (gender !== undefined && !Object.values(Gender).includes(gender)) {
    throw new Error('Nieprawidłowa płeć');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie znaleziony');
  }

  user.fitnessDetails = {
    age: age ?? user.fitnessDetails?.age,
    height: height ?? user.fitnessDetails?.height,
    weight: weight ?? user.fitnessDetails?.weight,
    gender: gender ?? user.fitnessDetails?.gender ?? Gender.none,
  };

  await user.save();

  return true;
};

export const getPrivacySettingsService = async (
  userId: string,
): Promise<IUser['privacySettings'] | null> => {
  const user = await User.findById(userId).select('privacySettings');
  return user?.privacySettings || null;
};

export const updatePrivacySettingsService = async (
  userId: string,
  newSettings: Partial<IUser['privacySettings']>,
): Promise<boolean> => {
  const updateFields: Record<string, unknown> = {};

  for (const [key, value] of Object.entries(newSettings)) {
    updateFields[`privacySettings.${key}`] = value;
  }

  const result = await User.findByIdAndUpdate(userId, { $set: updateFields });

  return !!result;
};

export const getFollowersService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: 'followers',
    select: 'username profileDetails.profilePicture',
  });

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  return user.followers;
};

export const getFollowingService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: 'followedUsers',
    select: 'username profileDetails.profilePicture',
  });

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  return user.followedUsers;
};

export const toggleUserFollowService = async (userId: string, targetUserId: string) => {
  if (!mongoose.Types.ObjectId.isValid(targetUserId)) {
    throw new Error('Nieprawidłowe ID użytkownika');
  }

  if (userId === targetUserId) {
    throw new Error('Nie można obserwować samego siebie');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const targetUser = await User.findById(targetUserId);

  if (!targetUser) {
    throw new Error('Nie znaleziono użytkownika do obserwowania');
  }

  const isFollowing = user.followedUsers.some(
    (followedId) => followedId.toString() === targetUserId,
  );

  if (isFollowing) {
    user.followedUsers = user.followedUsers.filter(
      (followedId) => followedId.toString() !== targetUserId,
    );

    targetUser.followers = targetUser.followers.filter(
      (followerId) => followerId.toString() !== userId,
    );

    await user.save();
    await targetUser.save();

    await sendNotificationService(
      `Użytkownik "${user.username}" przestał Cię obserwować.`,
      targetUser._id.toString(),
      Notifications.info,
      user.username,
    );

    return {
      message: 'Przestałeś obserwować użytkownika',
    };
  } else {
    user.followedUsers.push(targetUser._id);

    targetUser.followers.push(user._id);

    await user.save();
    await targetUser.save();

    await sendNotificationService(
      `Użytkownik "${user.username}" zaobserwował Cię.`,
      targetUser._id.toString(),
      Notifications.info,
      user.username,
    );

    await checkAndEarnBadges(targetUser._id.toString(), 'Followers');

    return {
      message: 'Zacząłeś obserwować użytkownika',
    };
  }
};

export const getUserProfileService = async (
  currentUserId: string,
  username: string,
): Promise<UserProfile> => {
  const user = await User.findOne({ username }).populate({
    path: 'badges.badge',
    model: Badge,
  });

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const profileUserId = user._id.toString();
  const isOwnProfile = currentUserId === profileUserId;

  const profile: UserProfile = {
    _id: profileUserId,
    username: user.username,
    profilePicture: user.profileDetails?.profilePicture || null,
    description: user.profileDetails?.description || null,
    followersCount: user.followers.length,
    followingCount: user.followedUsers.length,
    isBlocked: user?.isBlocked || null,
  };

  if (isOwnProfile || user.privacySettings.isGoalPublic) {
    profile.goal = user.profileDetails?.goal || null;
  }

  if (isOwnProfile || user.privacySettings.isPersonalDetailsPublic) {
    profile.personalDetails = {
      firstName: user.personalDetails?.firstName || null,
      lastName: user.personalDetails?.lastName || null,
      country: user.personalDetails?.country || null,
    };
  }

  if (isOwnProfile || user.privacySettings.isFitnessDetailsPublic) {
    profile.fitnessDetails = {
      height: user.fitnessDetails?.height || null,
      weight: user.fitnessDetails?.weight || null,
      age: user.fitnessDetails?.age || null,
      gender: user.fitnessDetails?.gender || null,
    };
  }

  if (isOwnProfile || user.privacySettings.isBadgesDetailsPublic) {
    const pinnedBadges =
      user.badges
        ?.filter((b) => b.isPinned && b.badge)
        .map((b) => ({
          _id: (b.badge as IBadge)._id.toString(),
          title: (b.badge as IBadge).title,
          description: (b.badge as IBadge).description,
          image: (b.badge as IBadge).image,
          awardedAt: b.awardedAt,
        })) || [];

    profile.pinnedBadges = pinnedBadges;
  }

  if (!isOwnProfile) {
    const currentUser = await User.findById(currentUserId);
    profile.isFollowing =
      currentUser?.followedUsers.some((id) => id.toString() === profileUserId) || false;
  }

  return profile;
};
