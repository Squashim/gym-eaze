import Notification from "../../models/notificationModel";
import { Notifications } from "../../constants/notifications";

export const seedNotifications = async () => {
  await Notification.deleteMany();
  await Notification.insertMany(notifications);
  console.log("Powiadomienia dodane");
};

const notifications = [
  {
    _id: "6887b9757bd7aefa4ba5edec",
    message:
      "Brawo! Otrzymałeś odznakę za pierwsze stworzone ćwiczenie. Gratulacje!",
    type: Notifications.Info,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
