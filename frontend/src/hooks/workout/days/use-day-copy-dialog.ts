import { useCallback, useState } from 'react';
import useOpen from '../../utils/use-open';
import type { TrainingDaySchema } from '@/schemas/workout-schema';

function useDayCopyDialog(onDayCopy: (source: number, target: number) => Promise<void>) {
  const dialog = useOpen();
  const [sourceDay, setSourceDay] = useState<TrainingDaySchema | null>(null);
  const [targetDayNumber, setTargetDayNumber] = useState<string>('');

  const handleOpenDialog = useCallback(
    (day: TrainingDaySchema) => {
      setSourceDay(day);
      dialog.open();
    },
    [dialog],
  );

  const handleCloseDialog = useCallback(() => {
    dialog.close();
    setTargetDayNumber('');
    setSourceDay(null);
  }, [dialog]);

  const handleConfirmCopy = useCallback(async () => {
    if (!sourceDay || !targetDayNumber) return;

    await onDayCopy(sourceDay.dayNumber, Number(targetDayNumber));
    handleCloseDialog();
  }, [onDayCopy, sourceDay, targetDayNumber, handleCloseDialog]);

  return {
    dialog,
    sourceDay,
    targetDayNumber,
    setTargetDayNumber,
    onOpenDialog: handleOpenDialog,
    onCloseDialog: handleCloseDialog,
    onConfirmCopy: handleConfirmCopy,
  };
}

export default useDayCopyDialog;
