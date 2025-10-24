import ScrollToTopButton from '@/components/ui/scroll-to-top-button';

interface ActiveWorkoutsTabProps {
  isOwnProfile: boolean;
  profileId: string;
  isViewingAsGuest: boolean;
}

function ActiveWorkoutsTab({ isOwnProfile, profileId, isViewingAsGuest }: ActiveWorkoutsTabProps) {
  return (
    <div className="flex flex-col gap-4 w-full h-full pb-4">
      <ScrollToTopButton />
    </div>
  );
}

export default ActiveWorkoutsTab;
