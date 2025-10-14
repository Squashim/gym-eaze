import type { UserPrivacySettings } from '@/types/user';
import { useMemo } from 'react';

function usePrivacyVisibility(
  isOwnProfile: boolean,
  isViewingAsGuest: boolean,
  privacySettings?: UserPrivacySettings,
) {
  return useMemo(
    () => ({
      shouldShowPersonalDetails: !(
        isOwnProfile &&
        isViewingAsGuest &&
        !privacySettings?.data?.isPersonalDetailsPublic
      ),
      shouldShowFitnessDetails: !(
        isOwnProfile &&
        isViewingAsGuest &&
        !privacySettings?.data?.isFitnessDetailsPublic
      ),
      shouldShowGoal: !(isOwnProfile && isViewingAsGuest && !privacySettings?.data?.isGoalPublic),
      shouldShowBadges: !(
        isOwnProfile &&
        isViewingAsGuest &&
        !privacySettings?.data?.isBadgesDetailsPublic
      ),
    }),
    [isOwnProfile, isViewingAsGuest, privacySettings],
  );
}

export default usePrivacyVisibility;
