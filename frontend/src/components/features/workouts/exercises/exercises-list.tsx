import { Skeleton } from '@/components/ui/skeleton';
import useGetExercisesByIds from '@/hooks/query/exercises/use-get-exercises-by-ids';
import type { WorkoutApiExercise, WorkoutExercise } from '@/types/workout';
import { DndContext } from '@dnd-kit/core';
import { restrictToParentElement, restrictToVerticalAxis } from '@dnd-kit/modifiers';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { useCallback, useMemo } from 'react';
import ExerciseItem from './exercise-item';
import ExerciseCopyDialog from './exercise-copy-dialog';
import useExerciseCopyDialog from '@/hooks/workout/exercises/use-exercise-copy.dialog';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Info } from 'lucide-react';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import { isDefaultExercise } from '@/lib/utils';

interface ExercisesListProps {
  exerciseIds: string[];
  formExercises: WorkoutExercise[];
  defaultExercisesMap?: Map<string, WorkoutApiExercise>;
  onExerciseRemove: (id: string) => void;
  onExerciseCopy: (sourceId: string, targetId: string) => void;
  onReorder: (newOrder: string[]) => void;
  onExerciseSelect: (id: string | null) => void;
}

const ExercisesList = ({
  exerciseIds,
  formExercises,
  defaultExercisesMap,
  onExerciseRemove,
  onExerciseCopy,
  onReorder,
  onExerciseSelect,
}: ExercisesListProps) => {
  const idsToFetch = useMemo(() => {
    if (!defaultExercisesMap) {
      return exerciseIds;
    }

    return exerciseIds.filter((id) => !defaultExercisesMap.has(id));
  }, [defaultExercisesMap, exerciseIds]);

  const { data: fetchedExercises, isLoading, isError } = useGetExercisesByIds(idsToFetch);

  const allExercises = useMemo(() => {
    const exerciseMap = new Map<string, GetExercisesByIdsApiResponse | WorkoutApiExercise>();

    if (defaultExercisesMap) {
      defaultExercisesMap.forEach((exercise, id) => {
        exerciseMap.set(id, exercise);
      });
    }

    if (fetchedExercises) {
      fetchedExercises.forEach((exercise) => {
        const exerciseId = exercise._id;

        if (!exerciseMap.has(exerciseId)) {
          exerciseMap.set(exerciseId, exercise);
        }
      });
    }

    return exerciseMap;
  }, [defaultExercisesMap, fetchedExercises]);

  const orderedExercises = useMemo(() => {
    return exerciseIds.map((id) => allExercises.get(id)).filter(Boolean) as (
      | GetExercisesByIdsApiResponse
      | WorkoutApiExercise
    )[];
  }, [exerciseIds, allExercises]);

  const {
    dialog,
    reorderedExercises,
    sourceExerciseId,
    targetExerciseId,
    setTargetExerciseId,
    onDragEnd,
    onOpenDialog,
    onCloseDialog,
    onConfirmCopy,
  } = useExerciseCopyDialog({
    exerciseIds,
    orderedExercises,
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

  if (isLoading && idsToFetch.length > 0) {
    return Array.from({ length: 3 }).map((_, index) => <Skeleton className="h-24" key={index} />);
  }

  if (isError) {
    return (
      <Alert>
        <Info />
        <AlertTitle>Wystąpił błąd podczas pobierania ćwiczeń.</AlertTitle>
      </Alert>
    );
  }

  return (
    <>
      <DndContext
        modifiers={[restrictToVerticalAxis, restrictToParentElement]}
        onDragEnd={onDragEnd}
      >
        <SortableContext items={exerciseIds} strategy={verticalListSortingStrategy}>
          <div className="flex flex-col gap-2">
            {reorderedExercises.map((exercise, index) => {
              const isDefault = isDefaultExercise(exercise);
              const exerciseId = isDefault ? exercise.originalId : exercise._id;
              const formExercise = formExercises.find((fe) => fe.id === exerciseId);
              const isLastExercise = index === reorderedExercises.length - 1;

              if (isDefault) {
                const setsCount = formExercise ? formExercise.sets.length : exercise.sets.length;
                const restAfterSets = formExercise
                  ? formExercise.restAfterSet
                  : exercise.restAfterSet;

                return (
                  <ExerciseItem
                    exercise={exercise}
                    exercisesCount={reorderedExercises.length}
                    id={exercise.originalId}
                    isLastExercise={isLastExercise}
                    key={exercise.originalId}
                    onDialogOpen={onOpenDialog}
                    onExerciseRemove={onExerciseRemove}
                    onSelect={onExerciseSelect}
                    restAfterSets={restAfterSets}
                    setsCount={setsCount}
                  />
                );
              } else {
                const setsCount = formExercise ? formExercise.sets.length : 0;
                const restAfterSets = formExercise ? formExercise.restAfterSet : 0;

                return (
                  <ExerciseItem
                    exercise={exercise}
                    exercisesCount={reorderedExercises.length}
                    id={exercise._id}
                    isLastExercise={isLastExercise}
                    key={exercise._id}
                    onDialogOpen={onOpenDialog}
                    onExerciseRemove={onExerciseRemove}
                    onSelect={onExerciseSelect}
                    restAfterSets={restAfterSets}
                    setsCount={setsCount}
                  />
                );
              }
            })}
          </div>
        </SortableContext>
      </DndContext>

      <ExerciseCopyDialog
        onClose={onCloseDialog}
        onCopy={onConfirmCopy}
        onOpenChange={handleDialogOpenChange}
        open={dialog.isOpen}
        orderedExercises={reorderedExercises}
        setTargetExerciseId={setTargetExerciseId}
        sourceExerciseId={sourceExerciseId}
        targetExerciseId={targetExerciseId}
      />
    </>
  );
};

export default ExercisesList;
