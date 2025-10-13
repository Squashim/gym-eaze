import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Info, X } from 'lucide-react';

interface GuestViewAlertProps {
  isVisible: boolean;
  onClose: () => void;
}

function GuestViewAlert({ isVisible, onClose }: GuestViewAlertProps) {
  if (!isVisible) return null;

  return (
    <Alert className="bg-secondary text-secondary-foreground">
      <Info />
      <AlertTitle className="line-clamp-none">
        Aktualnie przeglądasz wizytówkę swojego profilu jako gość. Niektóre informacje mogą być
        ukryte.
      </AlertTitle>
      <AlertDescription>
        <Button
          aria-label="Zamknij widok gościa"
          className="text-secondary-foreground ml-auto mt-1"
          onClick={onClose}
          title="Zamknij widok gościa"
          type="button"
        >
          Zamknij podgląd
        </Button>
      </AlertDescription>
    </Alert>
  );
}

export default GuestViewAlert;
