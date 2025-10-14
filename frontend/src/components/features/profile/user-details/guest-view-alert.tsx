import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Info } from 'lucide-react';

interface GuestViewAlertProps {
  isVisible: boolean;
  onClose: () => void;
}

function GuestViewAlert({ isVisible, onClose }: GuestViewAlertProps) {
  if (!isVisible) return null;

  return (
    <Alert className="bg-accent text-accent-foreground border-primary/20">
      <Info />
      <AlertTitle className="line-clamp-none">
        Przeglądasz swój profil jako gość. Niektóre informacje i funkcjonalności są ukryte, abyś
        mógł zobaczyć, jak inni użytkownicy widzą Twój profil.
      </AlertTitle>
      <AlertDescription>
        <Button
          aria-label="Zamknij widok gościa"
          className="w-full mt-2"
          onClick={onClose}
          title="Zamknij widok gościa"
          type="button"
          variant="secondary"
        >
          Zamknij podgląd
        </Button>
      </AlertDescription>
    </Alert>
  );
}

export default GuestViewAlert;
