type NotificationType = 'Informacja' | 'Błąd' | 'Ostrzeżenie';

export type Notification = {
  _id: string;
  message: string;
  type: NotificationType;
  createdAt: string;
  updatedAt: string;
};
