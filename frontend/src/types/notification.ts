type NotificationType = 'Informacja' | 'Błąd' | 'Ostrzeżenie';

export type Notification = {
  message: string;
  type: NotificationType;
  createdAt: string;
  updatedAt: string;
};
