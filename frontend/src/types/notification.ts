export type NotificationType = 'Informacja' | 'Błąd' | 'Ostrzeżenie';

export type CreateNotificationPayload = {
  message: string;
  type: NotificationType;
  from: string;
  to: string;
};

export type GetNotificationsApiResponse = {
  _id: string;
  message: string;
  from: string;
  type: NotificationType;
  createdAt: string;
  updatedAt: string;
};
