import { Notifications } from '../constants/notifications';
import Badge from '../models/badgeModel';
import Exercise from '../models/exerciseModel';
import Post from '../models/postModel';
import TrainingPlan from '../models/trainingPlanModel';
import User from '../models/userModel';
import { sendNotificationService } from './notificationService';

export const checkAndEarnBadges = async (userId: string, action: string) => {
  const user = await User.findById(userId);
  if (!user) {
    throw new Error('USER_NOT_FOUND');
  }

  let number = 0;
  switch (action) {
    case 'AccountAge': {
      const accountAgeInYears = Math.floor(
        (Date.now() - new Date(user.createdAt).getTime()) / (1000 * 60 * 60 * 24 * 365),
      );
      number = accountAgeInYears;
      break;
    }
    case 'Followers':
      number = user.followers.length;
      break;
    case 'Trainings':
      number = await TrainingPlan.countDocuments({ author: user._id });
      break;
    case 'Exercises':
      number = await Exercise.countDocuments({ author: user._id });
      break;
    case 'Posts':
      number = await Post.countDocuments({ author: user._id });
      break;
    default:
      number = 0;
  }

  const badges = await Badge.find({ 'requirements.action': action })
    .sort({ 'requirements.target': 1 })
    .lean();

  const ownedBadgeIds = new Set(user.badges?.map((badge) => badge.badge.toString()) || []);

  for (const badge of badges) {
    const badgeIdStr = badge._id.toString();

    // Użytkownik już posiada tę odznakę
    if (ownedBadgeIds.has(badgeIdStr)) {
      continue;
    }

    // Jeśli użytkownik spełnia wymagania odznaki, przyznajemy ją i powiadamiamy go o tym
    if (number >= badge.requirements.target) {
      await user.updateOne({
        $push: {
          badges: {
            badge: badge._id,
            awardedAt: new Date(),
          },
        },
      });
      ownedBadgeIds.add(badgeIdStr);

      await sendNotificationService(
        `Gratulacje! Zdobyłe/aś odznakę: "${badge.title}". Przypnij ją do swojego profilu, aby się nią pochwalić!`,
        user._id.toString(),
        Notifications.info,
        'System',
      );

      continue;
    }

    // Ponieważ odznaki są posortowane według wymagań, możemy przerwać pętlę, gdy napotkamy pierwszą niespełnioną
    break;
  }
};
