import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { useEffect, useRef } from 'react';

interface ReusableAlertDialogProps {
  open: boolean;
  title: string;
  description: string;
  variant?: 'default' | 'destructive';
  confirmLabel: string;
  cancelLabel: string;
  onClose: () => void;
  onConfirm: () => void;
  isLoading?: boolean;
}

const ReusableAlertDialog = ({
  open,
  title,
  description,
  variant = 'default',
  confirmLabel,
  cancelLabel,
  onClose,
  onConfirm,
  isLoading = false,
}: ReusableAlertDialogProps) => {
  const isLoadingRef = useRef(isLoading);
  const pendingCloseRef = useRef(false);

  useEffect(() => {
    isLoadingRef.current = isLoading;

    if (!isLoading && pendingCloseRef.current) {
      pendingCloseRef.current = false;
      onClose();
    }
  }, [isLoading, onClose]);

  const handleOpenChange = (nextOpen: boolean) => {
    if (!nextOpen) {
      if (isLoadingRef.current) {
        pendingCloseRef.current = true;
        return;
      }
      onClose();
    }
  };

  return (
    <AlertDialog onOpenChange={handleOpenChange} open={open}>
      <AlertDialogContent
        onEscapeKeyDown={(e) => {
          if (isLoadingRef.current) {
            e.preventDefault();
            pendingCloseRef.current = true;
          }
        }}
      >
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          <AlertDialogDescription>{description}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isLoading} onClick={onClose}>
            {cancelLabel}
          </AlertDialogCancel>
          <AlertDialogAction
            disabled={isLoading}
            onClick={(e) => {
              e.preventDefault();
              pendingCloseRef.current = true;
              onConfirm();
            }}
            variant={variant}
          >
            {isLoading ? 'Usuwanie...' : confirmLabel}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default ReusableAlertDialog;
