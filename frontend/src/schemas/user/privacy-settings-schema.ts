import { z } from 'zod/v3';

export const privacySettingsSchema = z.object({
  isPersonalDetailsPublic: z.boolean(),
  isFitnessDetailsPublic: z.boolean(),
  isBadgesDetailsPublic: z.boolean(),
  isGoalPublic: z.boolean(),
  isTrainingStatisticsPublic: z.boolean(),
});

export type PrivacySettingsSchema = z.infer<typeof privacySettingsSchema>;
