import { IBadge }  from "../models/badgeModel";
import Badge from "../models/badgeModel"; // Dodaj ten import
import cloudinary, { uploadExerciseMedia } from "../config/cloudinary";
import User, { IUser } from "../models/userModel";
import { Goal } from "../constants/goals";

export const getUserLikesService = async (userId: string) => {
  const user = await User.findById(userId).select("likes");
  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }
  return user.likes;
};

export const getUserFavoritesService = async (userId: string) => {
  const user = await User.findById(userId).select("favorites");
  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }
  return user.favorites;
};

export const getUserProfileDetailsService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: "badges.badge",
    model: Badge
  });

  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }

  const pinnedBadges =
    user.badges
      ?.filter(b => b.isPinned && b.badge)
      .map(b => ({
        id: (b.badge as IBadge)._id,
        title: (b.badge as IBadge).title || "",
        description: (b.badge as IBadge).description || "",
        image: (b.badge as IBadge).image || ""
      })) || [];

  return {
    profilePicture: user.profileDetails?.profilePicture || "",
    description: user.profileDetails?.description || "",
    goal: user.profileDetails?.goal || "",
    pinnedBadges,
    isBadgesDetailsPublic: user.privacySettings?.isBadgesDetailsPublic ?? true,
    isGoalPublic: user.privacySettings?.isGoalPublic ?? true
  };
};


export const updateUserProfileDetailsService = async (
  userId: string,
  description: string,
  goal: string | undefined,
  pinnedBadges: string[] | undefined,
  file?: Express.Multer.File
) => {
  const user = await User.findById(userId).populate("badges.badge");
  if (!user) {
    throw new Error("Użytkownik nie znaleziony");
  }

  let profilePicture = user.profileDetails?.profilePicture || "";
  if (file) {
    const uploaded = await uploadExerciseMedia(file.buffer, undefined);
    if (uploaded.imageUrl) {
      profilePicture = uploaded.imageUrl;
    }
  }

  if (Array.isArray(pinnedBadges)) {
    if (pinnedBadges.length > 5) {
      throw new Error("Możesz przypiąć maksymalnie 5 odznak");
    }

    (user.badges ?? []).forEach((b: any) => {
      const isPinned = pinnedBadges.includes(b.badge._id.toString());
      b.isPinned = isPinned;
    });
  }

  user.profileDetails = {
    ...user.profileDetails,
    description: description || user.profileDetails?.description || "",
    goal: (goal as Goal) ?? user.profileDetails?.goal,
    profilePicture,
  };

  await user.save();

  return { message: "Profil zaktualizowany pomyślnie" };
};

export const getUserBadgesService = async (userId: string) => {
  const user = await User.findById(userId).populate({
    path: "badges.badge",
    model: Badge
  });

  if (!user) {
    throw new Error("Nie znaleziono użytkownika");
  }

  const allBadges =
    user.badges
      ?.filter(b => b.badge)
      .map(b => ({
        id: (b.badge as IBadge)._id,
        title: (b.badge as IBadge).title,
        description: (b.badge as IBadge).description,
        image: (b.badge as IBadge).image
      })) || [];

  return allBadges;
};