import ProfileItemsDisplay from '@/components/features/profile/items-tabs/profile-items-display';
import ProfileDisplay from '@/components/features/profile/user-details/profile-display';
import PageLayout from '@/components/layout/page';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { STORAGE_FLAGS } from '@/constants/enums/storage-flags';
import { useUser } from '@/context/user';
import useGetUserProfile from '@/hooks/query/user/use-get-user-profile';
import { useSessionFlag } from '@/hooks/utils/use-session-flag';
import { useCallback, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

function Profile() {
  const { username } = useParams();
  const navigate = useNavigate();

  const { user } = useUser();
  const { isSet, removeFlag } = useSessionFlag(STORAGE_FLAGS.isFirstLogin);
  const { data: profileData, isLoading, isError, error } = useGetUserProfile(username ?? '');

  const [isViewingAsGuest, setIsViewingAsGuest] = useState(false);

  const isOwnProfile = useMemo(() => {
    return user?.username === profileData?.username;
  }, [user?.username, profileData?.username]);

  const effectiveIsOwnProfile = useMemo(() => {
    return isOwnProfile && !isViewingAsGuest;
  }, [isOwnProfile, isViewingAsGuest]);

  const handleViewAsGuestToggle = useCallback(() => {
    setIsViewingAsGuest((prev) => !prev);
  }, []);

  return (
    <>
      <PageLayout withFooter={false}>
        <section className="grid grid-cols-1 md:grid-cols-[0.65fr_1fr] gap-4 md:gap-6 w-full">
          <ProfileDisplay
            effectiveIsOwnProfile={effectiveIsOwnProfile}
            error={error}
            isError={isError}
            isLoading={isLoading}
            isOwnProfile={isOwnProfile}
            isViewingAsGuest={isViewingAsGuest}
            onViewAsGuestToggle={handleViewAsGuestToggle}
            profileData={profileData}
          />

          <ProfileItemsDisplay
            isOwnProfile={isOwnProfile}
            isProfileError={isError}
            isProfileLoading={isLoading}
            isViewingAsGuest={isViewingAsGuest}
            profileId={profileData?._id}
          />
        </section>
      </PageLayout>
      <ReusableAlertDialog
        cancelLabel="Pomiń"
        confirmLabel="Edytuj profil"
        description="Witamy w społeczności GymEaze! Wygląda na to, że to Twoje pierwsze logowanie. Jeśli chcesz, możesz teraz uzupełnić szczegóły swojego profilu. Życzymy miłych treningów!"
        onClose={removeFlag}
        onConfirm={() => {
          removeFlag();
          void navigate('/profile/edit', { replace: true });
        }}
        open={isSet}
        title={`Witaj ${user?.username}!`}
      />
    </>
  );
}

export default Profile;
