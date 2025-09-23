import { useCallback, useMemo, useState } from 'react';
import useOpen from '../../utils/use-open';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import type { DragEndEvent } from '@dnd-kit/core';
import { arrayMove } from '@dnd-kit/sortable';

interface UseExerciseCopyDialogProps {
  exerciseIds: string[];
  exercisesByIds: GetExercisesByIdsApiResponse[] | undefined;
  onExerciseCopy: (sourceId: string, targetId: string) => void;
  onReorder: (newOrder: string[]) => void;
}

function useExerciseCopyDialog({
  exerciseIds,
  exercisesByIds,
  onExerciseCopy,
  onReorder,
}: UseExerciseCopyDialogProps) {
  const dialog = useOpen();
  const [sourceExerciseId, setSourceExerciseId] = useState<string | null>(null);
  const [targetExerciseId, setTargetExerciseId] = useState<string>('');

  const orderedExercises = useMemo(() => {
    if (!exercisesByIds) return [];
    return exerciseIds
      .map((id) => exercisesByIds.find((ex) => ex._id === id))
      .filter(Boolean) as GetExercisesByIdsApiResponse[];
  }, [exerciseIds, exercisesByIds]);

  const handleDragEnd = useCallback(
    (event: DragEndEvent) => {
      const { active, over } = event;
      if (!over) return;
      if (active.id !== over.id) {
        const oldIndex = exerciseIds.findIndex((id) => id === active.id);
        const newIndex = exerciseIds.findIndex((id) => id === over.id);
        const newOrder = arrayMove(exerciseIds, oldIndex, newIndex);
        onReorder(newOrder);
      }
    },
    [exerciseIds, onReorder],
  );

  const handleOpenDialog = useCallback(
    (exerciseId: string) => {
      setSourceExerciseId(exerciseId);
      dialog.open();
    },
    [dialog],
  );

  const handleCloseDialog = useCallback(() => {
    dialog.close();
    setTargetExerciseId('');
    setSourceExerciseId(null);
  }, [dialog]);

  const handleConfirmCopy = useCallback(() => {
    if (!sourceExerciseId || !targetExerciseId) return;

    onExerciseCopy(sourceExerciseId, targetExerciseId);
    handleCloseDialog();
  }, [sourceExerciseId, targetExerciseId, onExerciseCopy, handleCloseDialog]);

  return {
    dialog,
    orderedExercises,
    sourceExerciseId,
    targetExerciseId,
    setTargetExerciseId,
    onDragEnd: handleDragEnd,
    onOpenDialog: handleOpenDialog,
    onCloseDialog: handleCloseDialog,
    onConfirmCopy: handleConfirmCopy,
  };
}

export default useExerciseCopyDialog;
