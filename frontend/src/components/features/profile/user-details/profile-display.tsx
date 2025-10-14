import useOpen from '@/hooks/utils/use-open';
import usePageTitle from '@/hooks/utils/use-page-title';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { InfoField } from '@/components/ui/info-fields';
import { Goal } from '@/constants/enums/goals';
import usePrivacyVisibility from '@/hooks/utils/use-privacy-visibility';
import { Skeleton } from '@/components/ui/skeleton';
import useGetPrivacySettings from '@/hooks/query/user/use-get-privacy-settings';
import type { UserProfile } from '@/types/user';
import { useCallback, useState } from 'react';
import Typography from '@/components/ui/typography';
import { getApiErrorMsg, getCountryCodeByName } from '@/lib/utils';
import FitnessRow from './fitness-row';
import { FollowUsersDialog, ProfileFollows } from './profile-follows';
import GuestViewAlert from './guest-view-alert';
import { Button } from '@/components/ui/button';
import { Link } from 'react-router-dom';
import { Loader2, Pencil } from 'lucide-react';
import { useUserFollow } from '@/hooks/query/user/use-user-follow';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { CircleFlag } from 'react-circle-flags';
import { Country } from '@/constants/enums/countries';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import PinnedBadges from './pinned-badges';

export type FollowsDialogType = 'followers' | 'following' | undefined;

interface ProfileDisplayProps {
  profileData?: UserProfile;
  isViewingAsGuest: boolean;
  effectiveIsOwnProfile: boolean;
  onViewAsGuestToggle: () => void;
  isLoading: boolean;
  isOwnProfile: boolean;
  isError: boolean;
  error: Error | null;
}

function ProfileDisplay({
  profileData,
  isViewingAsGuest,
  effectiveIsOwnProfile,
  onViewAsGuestToggle,
  isOwnProfile,
  isLoading,
  isError,
  error,
}: ProfileDisplayProps) {
  const followsDialog = useOpen();
  const { data: privacySettings } = useGetPrivacySettings({
    enabled: isOwnProfile,
  });
  const [followsDialogType, setFollowsDialogType] = useState<FollowsDialogType>(undefined);

  const visibility = usePrivacyVisibility(isOwnProfile, isViewingAsGuest, privacySettings);

  usePageTitle(`Profil ${profileData ? profileData.username : ''}`);

  const handleDialogShow = useCallback(
    (type: FollowsDialogType) => {
      if (isOwnProfile) {
        setFollowsDialogType(type);
        followsDialog.setIsOpen(true);
      }
    },
    [isOwnProfile, followsDialog],
  );

  if (isLoading) {
    return (
      <Card className="min-h-[50dvh] gap-4 p-4 ">
        <div className="flex items-center gap-3">
          <Skeleton className="size-20 rounded-full" />
          <Skeleton className="h-12 w-1/3" />
        </div>
        <CardContent className="grid gap-4 p-0">
          <Skeleton className="h-4 w-1/2" />
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-20 w-full" />
          <Skeleton className="h-6 w-1/4" />
          <Skeleton className="h-16 w-full" />
        </CardContent>
      </Card>
    );
  }

  if (isError || !profileData) {
    return (
      <Card className="min-h-[50dvh] gap-4 p-4  flex items-center justify-center">
        <Typography className="text-center" variant="h2">
          Ups! Coś poszło nie tak.
        </Typography>
        <Typography className="text-center text-balance" variant="h3">
          {getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie później.'}
        </Typography>
        <Button>
          <Link to="/">Wróć do strony głównej</Link>
        </Button>
      </Card>
    );
  }

  return (
    <>
      <Card className="min-h-[40dvh] gap-4 py-4 md:max-h-fit md:sticky md:top-20">
        <ProfileHeader
          isOwnProfile={isOwnProfile}
          isViewingAsGuest={isViewingAsGuest}
          profileData={profileData}
          shouldShowPersonalDetails={visibility.shouldShowPersonalDetails}
        />

        <CardContent className="grid gap-4 px-4">
          {visibility.shouldShowFitnessDetails && (
            <FitnessRow details={profileData.fitnessDetails} />
          )}

          <ProfileFollows
            followersCount={profileData.followersCount}
            followingCount={profileData.followingCount}
            isOwnProfile={effectiveIsOwnProfile}
            onClick={handleDialogShow}
          />

          <ProfileActions
            isFollowing={profileData.isFollowing}
            isOwnProfile={effectiveIsOwnProfile}
            isViewingAsGuest={isViewingAsGuest}
            onViewAsGuestToggle={onViewAsGuestToggle}
            profileId={profileData._id}
            profileUsername={profileData.username}
          />

          {profileData.description && (
            <InfoField label="O mnie">
              <Typography className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent text-sm text-accent-foreground">
                {profileData.description}
              </Typography>
            </InfoField>
          )}

          {profileData.goal && profileData.goal !== Goal.none && visibility.shouldShowGoal && (
            <InfoField label="Cel treningowy">
              <Badge className="bg-accent text-accent-foreground shadow-sm min-h-7">
                {profileData.goal}
              </Badge>
            </InfoField>
          )}

          {profileData.pinnedBadges.length > 0 && visibility.shouldShowBadges && (
            <InfoField label="Wyróżnione odznaki">
              <PinnedBadges badges={profileData.pinnedBadges} />
            </InfoField>
          )}

          <GuestViewAlert
            isVisible={isViewingAsGuest && isOwnProfile}
            onClose={onViewAsGuestToggle}
          />
        </CardContent>
      </Card>

      {effectiveIsOwnProfile && (
        <FollowUsersDialog
          count={
            followsDialogType === 'followers'
              ? profileData.followersCount
              : profileData.followingCount
          }
          onClose={() => {
            followsDialog.setIsOpen(false);
            setFollowsDialogType(undefined);
          }}
          onOpenChange={followsDialog.setIsOpen}
          open={followsDialog.isOpen}
          type={followsDialogType}
        />
      )}
    </>
  );
}

interface ProfileHeaderProps {
  profileData: UserProfile;
  isOwnProfile: boolean;
  isViewingAsGuest: boolean;
  shouldShowPersonalDetails: boolean;
}

function ProfileHeader({
  profileData,
  isOwnProfile,
  isViewingAsGuest,
  shouldShowPersonalDetails,
}: ProfileHeaderProps) {
  return (
    <header className="flex gap-3 px-4 flex-wrap">
      <div className="relative">
        <ReusableAvatar
          className="size-20 shadow-sm"
          fallbackClassName="dark:bg-background"
          profilePicture={profileData.profilePicture}
          username={profileData.username}
          variant="h3"
        />
        <CountryFlag
          country={profileData.personalDetails?.country}
          shouldShow={shouldShowPersonalDetails}
        />
      </div>

      <ProfileInfo
        isOwnProfile={isOwnProfile && !isViewingAsGuest}
        profileData={profileData}
        shouldShowPersonalDetails={shouldShowPersonalDetails}
      />
    </header>
  );
}

interface ProfileInfoProps {
  profileData: UserProfile;
  isOwnProfile: boolean;
  shouldShowPersonalDetails: boolean;
}

function ProfileInfo({ profileData, isOwnProfile, shouldShowPersonalDetails }: ProfileInfoProps) {
  const hasName = profileData.personalDetails?.lastName || profileData.personalDetails?.firstName;

  return (
    <div className="flex flex-col justify-center">
      <Typography variant="h3">
        {profileData.username} {isOwnProfile && '(Ty)'}
      </Typography>

      {hasName && shouldShowPersonalDetails && (
        <div className="flex gap-2 items-center">
          <Typography variant="muted">
            {profileData.personalDetails?.firstName} {profileData.personalDetails?.lastName}
          </Typography>
        </div>
      )}
    </div>
  );
}

interface CountryFlagProps {
  country?: string;
  shouldShow: boolean;
}

function CountryFlag({ country, shouldShow }: CountryFlagProps) {
  if (!country || country === Country.NONE || !shouldShow) {
    return null;
  }

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <CircleFlag
          className="absolute top-0 left-0 size-5 border border-primary/30 dark:border-border rounded-full shadow-md"
          countryCode={getCountryCodeByName(country).toLowerCase()}
          tabIndex={0}
        />
      </TooltipTrigger>
      <TooltipContent>
        <Typography className="leading-normal" variant="small">
          {country}
        </Typography>
      </TooltipContent>
    </Tooltip>
  );
}

interface ProfileActionsProps {
  isOwnProfile: boolean;
  isFollowing?: boolean;
  profileUsername: string;
  isViewingAsGuest: boolean;
  profileId: string;
  onViewAsGuestToggle: () => void;
}

function ProfileActions({
  isOwnProfile,
  isFollowing,
  profileId,
  isViewingAsGuest,
  profileUsername,
  onViewAsGuestToggle,
}: ProfileActionsProps) {
  const { toggleFollow } = useUserFollow(profileUsername);

  const handleFollow = () => {
    toggleFollow.mutate(profileId);
  };

  if (isOwnProfile) {
    return (
      <div className="flex items-center gap-3 flex-wrap">
        <Button asChild className="flex-1" type="button">
          <Link to="/profile/edit">
            <Pencil />
            Edytuj profil
          </Link>
        </Button>
        <Button onClick={onViewAsGuestToggle} type="button" variant="ghost">
          Wyświetl jako gość
        </Button>
      </div>
    );
  }

  return (
    <Button
      disabled={toggleFollow.isPending || isViewingAsGuest}
      onClick={handleFollow}
      type="button"
    >
      {toggleFollow.isPending ? (
        <>
          <Loader2 className="animate-spin" />
          <span>Ładowanie</span>
        </>
      ) : isFollowing ? (
        'Przestań obserwować'
      ) : (
        'Obserwuj'
      )}
    </Button>
  );
}

export default ProfileDisplay;
