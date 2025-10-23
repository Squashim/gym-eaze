import { Types } from 'mongoose';
import { UserRole } from '../constants/roles';
import User from '../models/userModel';
import { CommentParentType } from '../constants/commentType';
import { deleteExercise } from './exerciseService';
import { deleteTrainingPlan } from './trainingPlanService';
import { deletePostService } from './postService';
import { deleteCommentService } from './commentService';
import DeviceToken from '../models/deviceTokenModel';

export const blockUserService = async (id: string, userId: string) => {
  const admin = await User.findById(userId);

  if (!admin) {
    throw new Error('USER_NOT_FOUND');
  }

  if (admin.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (!Types.ObjectId.isValid(id)) {
    throw new Error('Nieprawidłowe ID użytkownika');
  }

  const userToBlock = await User.findById(id);

  if (!userToBlock) {
    throw new Error('USER_TO_BLOCK_NOT_FOUND');
  }
  await DeviceToken.deleteMany({ userId: userToBlock._id });
  userToBlock.isBlocked = true;
  await userToBlock.save();
};

export const deleteContentService = async (userId: string, itemType: string, itemId: string) => {
  const admin = await User.findById(userId);
  if (!admin) {
    throw new Error('USER_NOT_FOUND');
  }

  if (admin.role !== UserRole.admin) {
    throw new Error('NOT_ADMIN');
  }

  if (!Types.ObjectId.isValid(itemId)) {
    throw new Error('Nieprawidłowe ID zgłoszonej treści');
  }

  switch (itemType) {
    case CommentParentType.exercise:
      await deleteExercise(itemId, admin._id.toString());
      break;
    case CommentParentType.training:
      await deleteTrainingPlan(itemId, admin._id.toString());
      break;
    case CommentParentType.post:
      await deletePostService(itemId, admin._id.toString());
      break;
    case CommentParentType.comment:
      await deleteCommentService(itemId, admin._id.toString());
      break;
    default:
      throw new Error('Nieobsługiwany typ zgłoszonej treści');
  }

  return 'Zgłoszona treść została usunięta';
};
