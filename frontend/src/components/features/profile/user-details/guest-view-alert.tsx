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
    <Alert className="bg-accent text-accent-foreground border-primary/20 md:static md:shadow-none bottom-18 left-0 z-[50] shadow-lg fixed max-w-full w-[calc(100%-2rem)] mx-4 md:mx-0 md:w-full">
      <Info />
      <AlertTitle className="line-clamp-none">
        Przeglądasz swój profil jako gość. Niektóre informacje i funkcjonalności są ukryte.
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
