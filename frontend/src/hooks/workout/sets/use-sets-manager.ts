import { WORKOUT } from '@/constants/workout';
import type { WorkoutSchema } from '@/schemas/workout-schema';
import type { DurationSet, RepsSet, WorkoutSets } from '@/types/workout';
import { useCallback } from 'react';
import { useFormContext } from 'react-hook-form';
import { toast } from 'sonner';

interface UseSetsManagerProps {
  isRepsSet: boolean;
  dayNumber: number;
  exerciseNumber: number;
  sets: WorkoutSets[];
}

function useSetsManager({ dayNumber, exerciseNumber, sets, isRepsSet }: UseSetsManagerProps) {
  const { control, setValue } = useFormContext<WorkoutSchema>();

  const handleTotalSetsChange = useCallback(
    (newTotal: number) => {
      if (isRepsSet) {
        const repsSets = sets as RepsSet[];

        const additionalSets = Array.from(
          { length: Math.max(0, newTotal - repsSets.length) },
          () => ({
            repetitions: WORKOUT.DEFAULT_SETS_REPS,
            weight: undefined,
          }),
        );

        setValue(
          `stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`,
          newTotal > repsSets.length
            ? [...repsSets, ...additionalSets]
            : repsSets.slice(0, newTotal),
          { shouldDirty: true, shouldTouch: true },
        );
      } else {
        const durationSets = sets as DurationSet[];

        const additionalSets = Array.from(
          { length: Math.max(0, newTotal - durationSets.length) },
          () => ({
            duration: WORKOUT.DEFAULT_SETS_DURATION,
          }),
        );

        setValue(
          `stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`,
          newTotal > durationSets.length
            ? [...durationSets, ...additionalSets]
            : durationSets.slice(0, newTotal),
          { shouldDirty: true, shouldTouch: true },
        );
      }
    },
    [dayNumber, exerciseNumber, isRepsSet, sets, setValue],
  );

  const handleDuplicateSet = useCallback(
    (setNumber: number) => {
      if (sets.length === WORKOUT.MAX_SETS_PER_EXERCISE) return;

      if (isRepsSet) {
        const repsSets = sets as RepsSet[];
        const setToDuplicate = repsSets[setNumber];

        const newSets = [
          ...repsSets.slice(0, setNumber + 1),
          { ...setToDuplicate },
          ...repsSets.slice(setNumber + 1),
        ];

        setValue(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`, newSets, {
          shouldDirty: true,
          shouldTouch: true,
        });
      } else {
        const durationSets = sets as DurationSet[];
        const setToDuplicate = durationSets[setNumber];

        const newSets = [
          ...durationSets.slice(0, setNumber + 1),
          { ...setToDuplicate },
          ...durationSets.slice(setNumber + 1),
        ];

        setValue(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`, newSets, {
          shouldDirty: true,
          shouldTouch: true,
        });
      }

      toast.success('Seria została zduplikowana');
    },
    [dayNumber, exerciseNumber, isRepsSet, sets, setValue],
  );

  const handleSetDelete = useCallback(
    (setNumber: number) => {
      if (sets.length === 1) return;

      if (isRepsSet) {
        const repsSets = sets as RepsSet[];
        const newSets = repsSets.filter((_, i) => i !== setNumber);

        setValue(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`, newSets, {
          shouldDirty: true,
          shouldTouch: true,
        });
      } else {
        const durationSets = sets as DurationSet[];
        const newSets = durationSets.filter((_, i) => i !== setNumber);
        setValue(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`, newSets, {
          shouldDirty: true,
          shouldTouch: true,
        });
      }

      toast.success('Seria została usunięta');
    },
    [dayNumber, exerciseNumber, isRepsSet, sets, setValue],
  );

  return {
    control,
    onTotalSetsChange: handleTotalSetsChange,
    onDuplicate: handleDuplicateSet,
    onDelete: handleSetDelete,
  };
}

export default useSetsManager;
