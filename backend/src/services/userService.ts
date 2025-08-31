import User from "../models/userModel";

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