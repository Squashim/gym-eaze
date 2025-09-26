import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import { STORAGE_FLAGS } from '@/constants/storage-flags';
import { useUser } from '@/context/user';
import { useSessionFlag } from '@/hooks/utils/use-session-flag';

const Home = () => {
  const { user } = useUser();
  const { isSet, removeFlag } = useSessionFlag(STORAGE_FLAGS.isFirstLogin);

  return (
    <>
      <ReusableAlertDialog
        cancelLabel="Pomiń"
        confirmLabel="Edytuj profil"
        description="Witamy w społeczności GymEaze! Wygląda na to, że to Twoje pierwsze logowanie. Jeśli chcesz, możesz teraz uzupełnić szczegóły swojego profilu. Życzymy miłych treningów!"
        onClose={removeFlag}
        onConfirm={() => {
          removeFlag();
          console.log('Przejdź do edycji profilu');
        }}
        open={isSet}
        title={`Witaj ${user?.username}!`}
      />
      <div>home page</div>
    </>
  );
};

export default Home;
