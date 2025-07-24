// mozna jako enum
export const badgeActions = [
  "Days",
  "Followers",
  "Trainings",
  "Sensor",
  "Exercises",
  "Posts",
] as const;

export type BadgeAction = (typeof badgeActions)[number];
