import type { WorkoutSchema } from '@/schemas/workout-schema';
import { useFormContext } from 'react-hook-form';
import useOpen from '../../utils/use-open';
import { useCallback, useMemo } from 'react';
import { getWorkoutDefaultSets } from '@/lib/utils';

interface UseActiveExerciseManagerProps {
  exerciseIds: string[];
  exerciseId: string;
  dayNumber: number;
  onExerciseSelect: (id: string | null) => void;
}

function useActiveExerciseManager({
  exerciseIds,
  exerciseId,
  dayNumber,
  onExerciseSelect,
}: UseActiveExerciseManagerProps) {
  const { control, trigger, watch, setValue } = useFormContext<WorkoutSchema>();
  const alertDialog = useOpen();

  const exerciseNumber = useMemo(() => {
    return exerciseIds.findIndex((id) => id === exerciseId);
  }, [exerciseId, exerciseIds]);

  const sets = watch(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`);
  const isRepsSet = watch(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.isRepsSet`);

  const handleAlertConfirm = useCallback(async () => {
    const newIsRepsSet = !isRepsSet;

    setValue(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.isRepsSet`, newIsRepsSet, {
      shouldDirty: true,
      shouldTouch: true,
    });

    if (newIsRepsSet) {
      setValue(
        `stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`,
        [...getWorkoutDefaultSets(newIsRepsSet)],
        { shouldValidate: true, shouldDirty: true },
      );
    } else {
      setValue(
        `stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`,
        [...getWorkoutDefaultSets(newIsRepsSet)],
        { shouldValidate: true, shouldDirty: true },
      );
    }

    await trigger(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets`);
    alertDialog.close();
  }, [dayNumber, exerciseNumber, isRepsSet, setValue, trigger, alertDialog]);

  const handleSwitchChange = useCallback(() => {
    alertDialog.open();
  }, [alertDialog]);

  const handleBack = useCallback(() => {
    onExerciseSelect(null);
  }, [onExerciseSelect]);

  const isLastExercise = useMemo(() => {
    return exerciseIds.findIndex((id) => id === exerciseId) === exerciseIds.length - 1;
  }, [exerciseId, exerciseIds]);

  return {
    onSwitchChange: handleSwitchChange,
    onAlertConfirm: handleAlertConfirm,
    onBack: handleBack,
    exerciseNumber,
    alertDialog,
    control,
    sets,
    isRepsSet,
    isLastExercise,
  };
}

export default useActiveExerciseManager;
