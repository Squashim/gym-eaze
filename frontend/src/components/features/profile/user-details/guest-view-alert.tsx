import { Alert, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Info, X } from 'lucide-react';

interface GuestViewAlertProps {
  isVisible: boolean;
  onClose: () => void;
}

function GuestViewAlert({ isVisible, onClose }: GuestViewAlertProps) {
  if (!isVisible) return null;

  return (
    <Alert className="fixed bottom-4 left-1/2 -translate-x-1/2 z-50 w-[calc(100vw_-_2rem)] max-w-lg shadow-lg flex items-start gap-3">
      <div>
        <Info className="size-4" />
      </div>
      <AlertTitle className="line-clamp-none">
        Aktualnie przeglądasz swój profil jako gość. Niektóre informacje mogą być ukryte.
      </AlertTitle>
      <Button
        aria-label="Zamknij widok gościa"
        className="shadow-lg"
        onClick={onClose}
        size="icon"
        title="Zamknij widok gościa"
        type="button"
        variant="secondary"
      >
        <X />
      </Button>
    </Alert>
  );
}

export default GuestViewAlert;
