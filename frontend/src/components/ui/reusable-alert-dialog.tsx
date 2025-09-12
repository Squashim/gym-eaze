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
  return (
    <AlertDialog
      onOpenChange={(isOpen) => {
        if (!isOpen && !isLoading) onClose();
      }}
      open={open}
    >
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          <AlertDialogDescription>{description}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isLoading} onClick={onClose}>
            {cancelLabel}
          </AlertDialogCancel>
          <AlertDialogAction disabled={isLoading} onClick={onConfirm} variant={variant}>
            {isLoading ? 'Usuwanie...' : confirmLabel}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default ReusableAlertDialog;
