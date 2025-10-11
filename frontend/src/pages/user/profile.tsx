import PageLayout from '@/components/layout/page';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { STORAGE_FLAGS } from '@/constants/enums/storage-flags';
import { useUser } from '@/context/user';
import { useSessionFlag } from '@/hooks/utils/use-session-flag';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const { user } = useUser();
  const navigate = useNavigate();

  const { isSet, removeFlag } = useSessionFlag(STORAGE_FLAGS.isFirstLogin);
  return (
    <>
      <PageLayout title="Profil"></PageLayout>
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
