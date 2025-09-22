import Message from '@/components/ui/message';
import { Skeleton } from '@/components/ui/skeleton';
import useGetExercisesByIds from '@/hooks/query/exercises/use-get-exercises-by-ids';
import type { WorkoutExercise } from '@/types/workout';
import { DndContext } from '@dnd-kit/core';
import { restrictToParentElement, restrictToVerticalAxis } from '@dnd-kit/modifiers';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useCallback } from 'react';
import ExerciseItem from './exercise-item';
import ExerciseCopyDialog from './exercise-copy-dialog';
import useExerciseCopyDialog from '@/hooks/workout/exercises/use-exercise-copy.dialog';

interface ExercisesListProps {
  exerciseIds: string[];
  formExercises: WorkoutExercise[];
  onExerciseRemove: (id: string) => void;
  onExerciseCopy: (sourceId: string, targetId: string) => void;
  onReorder: (newOrder: string[]) => void;
  onExerciseSelect: (id: string | null) => void;
}

const ExercisesList = ({
  exerciseIds,
  formExercises,
  onExerciseRemove,
  onExerciseCopy,
  onReorder,
  onExerciseSelect,
}: ExercisesListProps) => {
  const { data: exercisesByIds, isLoading, isError } = useGetExercisesByIds(exerciseIds);

  const {
    dialog,
    orderedExercises,
    sourceExerciseId,
    targetExerciseId,
    setTargetExerciseId,
    onDragEnd,
    onOpenDialog,
    onCloseDialog,
    onConfirmCopy,
  } = useExerciseCopyDialog({
    exerciseIds,
    exercisesByIds,
    onExerciseCopy,
    onReorder,
  });

  const handleDialogOpenChange = useCallback(
    (open: boolean) => {
      if (open) {
        dialog.open();
      } else {
        onCloseDialog();
      }
    },
    [dialog, onCloseDialog],
  );

  if (isLoading) {
    return Array.from({ length: 3 }).map((_, index) => <Skeleton className="h-24" key={index} />);
  }

  if (isError) {
    return <Message>Wystąpił błąd podczas pobierania ćwiczeń.</Message>;
  }

  return (
    <>
      <DndContext
        modifiers={[restrictToVerticalAxis, restrictToParentElement]}
        onDragEnd={onDragEnd}
      >
        <SortableContext items={exerciseIds} strategy={verticalListSortingStrategy}>
          <div className="flex flex-col gap-2">
            {orderedExercises.map((exercise) => {
              const formExercise = formExercises.find((fe) => fe.id === exercise._id);
              const setsCount = formExercise ? formExercise.sets.length : 0;
              const restAfterSets = formExercise ? formExercise.restAfterSet : 0;

              return (
                <ExerciseItem
                  exercise={exercise}
                  exercisesCount={orderedExercises.length}
                  id={exercise._id}
                  key={exercise._id}
                  onDialogOpen={onOpenDialog}
                  onExerciseRemove={onExerciseRemove}
                  onSelect={onExerciseSelect}
                  restAfterSets={restAfterSets}
                  setsCount={setsCount}
                />
              );
            })}
          </div>
        </SortableContext>
      </DndContext>

      <ExerciseCopyDialog
        onClose={onCloseDialog}
        onCopy={onConfirmCopy}
        onOpenChange={handleDialogOpenChange}
        open={dialog.isOpen}
        orderedExercises={orderedExercises}
        setTargetExerciseId={setTargetExerciseId}
        sourceExerciseId={sourceExerciseId}
        targetExerciseId={targetExerciseId}
      />
    </>
  );
};

export default ExercisesList;
