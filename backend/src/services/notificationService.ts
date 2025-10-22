import { FilterQuery, Types } from 'mongoose';
import { Notifications, notificationsValues } from '../constants/notifications';
import { UserRole } from '../constants/roles';
import User from '../models/userModel';
import Notification, { INotification } from '../models/notificationModel';

export const sendNotificationService = async (
  message: string,
  to: string,
  type: Notifications,
  from: string,
  userId?: string,
) => {
  if (!message || message.trim() === '') {
    throw new Error('Wiadomość powiadomienia nie może być pusta');
  }

  if (!from || from.trim() === '') {
    throw new Error('Nadawca powiadomienia jest wymagany');
  }

  if (!notificationsValues.includes(type)) {
    throw new Error('Nieprawidłowy typ powiadomienia');
  }

  if (userId) {
    const user = await User.findById(userId);

    if (!user) {
      throw new Error('Nie znaleziono użytkownika');
    }

    if (user.role !== UserRole.admin) {
      throw new Error('Brak uprawnień do wysyłania powiadomień');
    }

    if (to === userId) {
      throw new Error('Nie można wysłać powiadomienia do samego siebie');
    }
  }

  if (!Types.ObjectId.isValid(to)) {
    throw new Error('Nieprawidłowy identyfikator odbiorcy powiadomienia');
  }

  const recipient = await User.findById(to);

  if (!recipient) {
    throw new Error('Nie znaleziono odbiorcy powiadomienia');
  }

  const notification = new Notification({
    message: message.trim(),
    from: from.trim(),
    to: new Types.ObjectId(to),
    type,
  });

  await notification.save();

  if (!Array.isArray(recipient.notifications)) {
    recipient.notifications = [];
  }
  recipient.notifications.push(notification._id as Types.ObjectId);
  await recipient.save();

  return notification;
};

export const getNotificationsService = async (
  limit: number = 10,
  userId: string,
  cursor?: string,
) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  const total = user.notifications ? user.notifications.length : 0;

  const query: FilterQuery<INotification> = {
    _id: { $in: user.notifications || [] },
  };

  if (cursor) {
    query._id = {
      $in: user.notifications || [],
      $lt: new Types.ObjectId(cursor),
    };
  }

  const items = await Notification.find(query)
    .sort({ createdAt: -1, _id: -1 })
    .limit(limit + 1)
    .lean();

  const hasMore = items.length > limit;
  if (hasMore) {
    items.pop();
  }

  const nextCursor = hasMore && items.length > 0 ? items[items.length - 1]._id.toString() : null;

  return {
    items,
    hasMore,
    nextCursor,
    total,
  };
};

export const deleteNotificationService = async (userId: string) => {
  const user = await User.findById(userId);

  if (!user) {
    throw new Error('Nie znaleziono użytkownika');
  }

  await Notification.deleteMany({ _id: { $in: user.notifications || [] } });
  user.notifications = [];
  await user.save();

  return 'Powiadomienia zostały wyczyszczone';
};
