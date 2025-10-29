import mongoose, { Types } from 'mongoose';
import User from '../models/userModel';
import UserHasTrainingPlans from '../models/userHasTrainingPlansModel';
import { StatusTraining } from '../constants/statuses';
import TrainingPlan from '../models/trainingPlanModel';
import Exercise from '../models/exerciseModel';

export const getStartedTrainingPlansService = async (
  targetUserId: string,
  page: number,
  pageSize: number,
  includePrivate: boolean,
  currentUserId: string,
) => {
  if (!mongoose.Types.ObjectId.isValid(targetUserId)) {
    throw new Error('Nieprawidłowy identyfikator użytkownika');
  }

  const user = await User.findById(targetUserId);

  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const isPublic = user.privacySettings.isTrainingStatisticsPublic;
  const isOwnProfile = targetUserId === currentUserId;

  if (!isOwnProfile && !isPublic) {
    return { items: [], total: 0, page, pageSize };
  }

  if (isOwnProfile && !includePrivate && !isPublic) {
    return { items: [], total: 0, page, pageSize };
  }

  const skip = (page - 1) * pageSize;

  const startedTrainingIds = user.statistics.startedTrainingPlans.map((stp) => stp.trainingPlanId);

  const [items, total] = await Promise.all([
    UserHasTrainingPlans.find({ _id: { $in: startedTrainingIds } })
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(pageSize)
      .select(
        'trainingPlanSnapshot.originalID trainingPlanSnapshot.name trainingPlanSnapshot.image trainingPlanSnapshot.goal trainingPlanSnapshot.advancedLevel trainingPlanSnapshot.author trainingPlanSnapshot.days progress createdAt',
      )
      .lean(),
    UserHasTrainingPlans.countDocuments({ _id: { $in: startedTrainingIds } }),
  ]);

  const itemsWithFullInfo = items.map((item: any) => {
    const userTraining = user.statistics.startedTrainingPlans.find(
      (stp) => stp.trainingPlanId.toString() === item._id.toString(),
    );

    const snapshot = item.trainingPlanSnapshot;
    const progress = item.progress;
    const totalDays = snapshot.days.length;
    const restDays = snapshot.days.filter((day: any) => day.isRestDay).length;
    const trainingDays = totalDays - restDays;
    const completedDaysCount = progress.completedDays.length;

    return {
      _id: item._id,
      originalId: snapshot.originalId,
      name: snapshot.name,
      image: snapshot.image,
      goal: snapshot.goal,
      advancedLevel: snapshot.advancedLevel,
      author: snapshot.author,
      status: userTraining?.status || StatusTraining.active,
      totalDays,
      trainingDays,
      restDays,
      completedDays: completedDaysCount,
      createdAt: item.createdAt,
    };
  });

  return { items: itemsWithFullInfo, total, page, pageSize };
};

export const startTrainingPlanService = async (userId: string, trainingPlanId: string) => {
  if (!mongoose.Types.ObjectId.isValid(trainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const trainingPlan = await TrainingPlan.findById(trainingPlanId)
    .populate('author', 'username profileDetails.profilePicture')
    .populate('days.exercises.id');

  if (!trainingPlan) {
    throw new Error('Plan treningowy nie został znaleziony');
  }

  const startedTrainingIds = user.statistics.startedTrainingPlans.map((stp) => stp.trainingPlanId);

  if (startedTrainingIds.length > 0) {
    const existingUserTrainingPlans = await UserHasTrainingPlans.find({
      _id: { $in: startedTrainingIds },
      'trainingPlanSnapshot.originalId': trainingPlanId,
    });

    if (existingUserTrainingPlans.length > 0) {
      throw new Error('Ten plan treningowy jest już rozpoczęty');
    }
  }

  const exerciseIds = trainingPlan.days.flatMap((day) => day.exercises.map((ex) => ex.id));

  const exercises = await Exercise.find({ _id: { $in: exerciseIds } })
    .populate('muscleGroup', 'name')
    .populate('equipment', 'name')
    .lean();

  const exercisesMap = new Map(exercises.map((ex: any) => [ex._id.toString(), ex]));

  const trainingPlanSnapshot = {
    originalId: trainingPlan._id,
    name: trainingPlan.name,
    description: trainingPlan.description,
    image: trainingPlan.image,
    goal: trainingPlan.goal,
    advancedLevel: trainingPlan.advancedLevel,
    author: {
      id: (trainingPlan.author as any)._id,
      username: (trainingPlan.author as any).username,
      profilePicture: (trainingPlan.author as any).profileDetails?.profilePicture,
    },
    days: trainingPlan.days.map((day) => ({
      name: day.name,
      dayNumber: day.dayNumber,
      totalDuration: day.totalDuration,
      isRestDay: day.isRestDay,
      exercises: day.exercises.map((ex) => {
        const exerciseId = (ex.id as { _id: Types.ObjectId })._id;
        const exerciseData: any = exercisesMap.get(exerciseId.toString());

        if (!exerciseData) {
          throw new Error(`Ćwiczenie o ID ${exerciseId} nie zostało znalezione`);
        }

        return {
          originalId: exerciseId,
          name: exerciseData.name,
          description: exerciseData.description,
          image: exerciseData.image,
          video: exerciseData.video,
          advancedLevel: exerciseData.advancedLevel,
          muscleGroup: Array.isArray(exerciseData.muscleGroup)
            ? exerciseData.muscleGroup.map((mg: any) => ({
                id: mg._id,
                name: mg.name,
              }))
            : [],
          equipment: Array.isArray(exerciseData.equipment)
            ? exerciseData.equipment.map((eq: any) => ({
                id: eq._id,
                name: eq.name,
              }))
            : [],
          isRepsSet: ex.isRepsSet,
          duration: ex.duration,
          restAfterSet: ex.restAfterSet,
          sets: ex.sets,
        };
      }),
    })),
  };

  const userTrainingPlan = new UserHasTrainingPlans({
    userId: user._id,
    trainingPlanSnapshot,
    progress: {
      totalDays: trainingPlan.days.length,
      completedDays: [],
    },
  });

  await userTrainingPlan.save();

  user.statistics.startedTrainingPlans.push({
    trainingPlanId: userTrainingPlan._id as Types.ObjectId,
    status: StatusTraining.active,
  });

  await user.save();

  return {
    message: 'Plan treningowy został rozpoczęty',
    userTrainingPlanId: userTrainingPlan._id,
  };
};

export const deleteStartedTrainingPlanService = async (
  userId: string,
  userTrainingPlanId: string,
) => {
  if (!mongoose.Types.ObjectId.isValid(userTrainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const userTrainingPlan = await UserHasTrainingPlans.findByIdAndDelete(userTrainingPlanId);
  if (!userTrainingPlan) {
    throw new Error('Rozpoczęty plan treningowy nie został znaleziony');
  }

  user.statistics.startedTrainingPlans = user.statistics.startedTrainingPlans.filter(
    (stp) => stp.trainingPlanId.toString() !== userTrainingPlanId,
  );

  await user.save();
};

export const resetStartedTrainingPlanService = async (
  userId: string,
  userTrainingPlanId: string,
) => {
  if (!mongoose.Types.ObjectId.isValid(userTrainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const userTrainingPlan = await UserHasTrainingPlans.findById(userTrainingPlanId);
  if (!userTrainingPlan) {
    throw new Error('Rozpoczęty plan treningowy nie został znaleziony');
  }

  userTrainingPlan.progress.completedDays = [];
  userTrainingPlan.completedAt = undefined;
  await userTrainingPlan.save();

  const startedTraining = user.statistics.startedTrainingPlans.find(
    (stp) => stp.trainingPlanId.toString() === userTrainingPlanId,
  );

  if (startedTraining) {
    startedTraining.status = StatusTraining.active;
    await user.save();
  }

  return {
    message: 'Plan treningowy został zresetowany',
  };
};

export const getStartedTrainingPlanDaysService = async (
  userId: string,
  userTrainingPlanId: string,
) => {
  if (!mongoose.Types.ObjectId.isValid(userTrainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const userTrainingPlan = await UserHasTrainingPlans.findById(userTrainingPlanId)
    .select('trainingPlanSnapshot.days progress.completedDays')
    .lean();

  if (!userTrainingPlan) {
    throw new Error('Rozpoczęty plan treningowy nie został znaleziony');
  }

  const snapshot = (userTrainingPlan as any).trainingPlanSnapshot;
  const progress = (userTrainingPlan as any).progress;

  const days = snapshot.days.map((day: any) => {
    const isCompleted = progress.completedDays.some((cd: any) => cd.dayNumber === day.dayNumber);

    return {
      dayNumber: day.dayNumber,
      name: day.name,
      totalDuration: day.totalDuration,
      isRestDay: day.isRestDay,
      isCompleted,
    };
  });

  return { days };
};

export const getStartedTrainingPlanDayDetailsService = async (
  userId: string,
  userTrainingPlanId: string,
  dayNumber: number,
) => {
  if (!mongoose.Types.ObjectId.isValid(userTrainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const user = await User.findById(userId);
  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  const userTrainingPlan = await UserHasTrainingPlans.findById(userTrainingPlanId).lean();

  if (!userTrainingPlan) {
    throw new Error('Rozpoczęty plan treningowy nie został znaleziony');
  }

  const snapshot = (userTrainingPlan as any).trainingPlanSnapshot;
  const progress = (userTrainingPlan as any).progress;

  const day = snapshot.days.find((d: any) => d.dayNumber === dayNumber);

  if (!day) {
    throw new Error('Dzień treningowy nie został znaleziony');
  }

  const exercises = day.exercises.map((exercise: any) => ({
    originalId: exercise.originalId,
    name: exercise.name,
    description: exercise.description,
    image: exercise.image,
    video: exercise.video,
    muscleGroup: exercise.muscleGroup,
    equipment: exercise.equipment,
    isRepsSet: exercise.isRepsSet,
    duration: exercise.duration,
    restAfterSet: exercise.restAfterSet,
    sets: exercise.sets,
  }));

  return {
    dayNumber: day.dayNumber,
    name: day.name,
    totalDuration: day.totalDuration,
    exercises,
  };
};

export const completeDayTrainingService = async (
  userId: string,
  userTrainingPlanId: string,
  dayNumber: number,
) => {
  if (!mongoose.Types.ObjectId.isValid(userTrainingPlanId)) {
    throw new Error('Nieprawidłowy identyfikator planu treningowego');
  }

  const [user, userTrainingPlan] = await Promise.all([
    User.findById(userId),
    UserHasTrainingPlans.findById(userTrainingPlanId),
  ]);

  if (!user) {
    throw new Error('Użytkownik nie został znaleziony');
  }

  if (!userTrainingPlan) {
    throw new Error('Rozpoczęty plan treningowy nie został znaleziony');
  }

  const day = userTrainingPlan.trainingPlanSnapshot.days.find((d) => d.dayNumber === dayNumber);

  if (!day) {
    throw new Error('Dzień treningowy nie został znaleziony');
  }

  const alreadyCompleted = userTrainingPlan.progress.completedDays.some(
    (cd) => cd.dayNumber === dayNumber,
  );

  if (alreadyCompleted) {
    throw new Error('Ten dzień jest już ukończony');
  }

  const completedDay = {
    dayNumber,
    exercises: day.exercises.map((exercise) => ({
      exerciseOriginalId: exercise.originalId,
      sets: exercise.sets.map((set: any) => {
        let time = 0;
        if ('repetitions' in set) {
          time = set.repetitions * 3;
        } else if ('duration' in set) {
          time = set.duration;
        }
        return { time };
      }),
    })),
  };

  userTrainingPlan.progress.completedDays.push(completedDay);

  const totalDayTime = completedDay.exercises.reduce(
    (sum, ex) => sum + ex.sets.reduce((s, set) => s + (set.time || 0), 0),
    0,
  );

  user.statistics.totalTrainingsTime += totalDayTime;

  const allDaysCompleted = userTrainingPlan.trainingPlanSnapshot.days.every((day) => {
    if (day.isRestDay) return true;
    return userTrainingPlan.progress.completedDays.some((cd) => cd.dayNumber === day.dayNumber);
  });

  if (allDaysCompleted) {
    userTrainingPlan.completedAt = new Date();
    const startedTraining = user.statistics.startedTrainingPlans.find(
      (stp) => stp.trainingPlanId.toString() === userTrainingPlanId,
    );
    if (startedTraining) {
      startedTraining.status = StatusTraining.finished;
    }
  }

  await Promise.all([userTrainingPlan.save(), user.save()]);

  return {
    message: 'Dzień treningowy został ukończony',
    isTrainingCompleted: allDaysCompleted,
  };
};
