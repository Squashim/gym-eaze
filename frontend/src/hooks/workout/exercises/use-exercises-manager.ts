import type { TrainingDaySchema, WorkoutSchema } from '@/schemas/workout-schema';
import type { WorkoutExercise } from '@/types/workout';
import { useCallback, useMemo } from 'react';
import { useFormContext } from 'react-hook-form';
import { toast } from 'sonner';
import { WORKOUT } from '@/constants/workout';
import { getWorkoutDefaultSets } from '@/lib/utils';
import useOpen from '../../utils/use-open';

interface UseExercisesManagerProps {
  dayNumber: number;
  onDaySelect: (day: TrainingDaySchema | null) => void;
}

function useExercisesManager({ dayNumber, onDaySelect }: UseExercisesManagerProps) {
  const alertDialog = useOpen();
  const {
    control,
    getValues,
    setValue,
    watch,
    trigger,
    formState: { errors },
  } = useFormContext<WorkoutSchema>();

  const isRestDay = watch(`stepTwo.days.${dayNumber}.isRestDay`);
  const formExercises = watch(`stepTwo.days.${dayNumber}.exercises`);

  const exerciseIds = useMemo(() => formExercises.map((exercise) => exercise.id), [formExercises]);

  const handleExerciseRemove = useCallback(
    (id: string) => {
      const updatedExercises = formExercises.filter((exercise) => exercise.id !== id);

      setValue(`stepTwo.days.${dayNumber}.exercises`, updatedExercises, {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });

      toast.success(`Usunięto ćwiczenie z dnia`);
    },
    [setValue, dayNumber, formExercises],
  );

  const handleExerciseToggle = useCallback(
    (id: string) => {
      if (formExercises.length >= WORKOUT.MAX_EXERCISES_PER_DAY) {
        toast.error('Nie możesz dodać więcej niż 15 ćwiczeń do dnia treningowego.');
        return;
      }

      const exerciseExists = formExercises.some((e) => e.id === id);
      if (exerciseExists) {
        handleExerciseRemove(id);
        return;
      }

      const newExercise: WorkoutExercise = {
        id,
        isRepsSet: true,
        restAfterSet: WORKOUT.DEFAULT_REST_AFTER_SET,
        sets: getWorkoutDefaultSets(true),
      };

      setValue(`stepTwo.days.${dayNumber}.exercises`, [...formExercises, newExercise], {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });

      toast.success(`Dodano ćwiczenie do dnia`);
      return;
    },
    [dayNumber, formExercises, setValue, handleExerciseRemove],
  );

  const handleReorder = useCallback(
    (newOrder: string[]) => {
      const reordered = newOrder.map((id) => formExercises.find((ex) => ex.id === id)!);
      setValue(`stepTwo.days.${dayNumber}.exercises`, reordered, {
        shouldDirty: true,
        shouldTouch: true,
      });
    },
    [dayNumber, formExercises, setValue],
  );

  const isExerciseAdded = useCallback(
    (id: string) => {
      return formExercises.some((e) => e.id === id);
    },
    [formExercises],
  );

  const handleExerciseCopy = useCallback(
    (sourceId: string, targetId: string) => {
      const sourceExercise = formExercises.find((ex) => ex.id === sourceId);

      if (!sourceExercise) {
        toast.error('Nie znaleziono ćwiczenia do skopiowania.');
        return;
      }

      const updatedExercises = formExercises.map((ex) => {
        if (ex.id !== targetId) return ex;

        if (sourceExercise.isRepsSet) {
          return {
            ...ex,
            isRepsSet: true as const,
            sets: sourceExercise.sets,
            restAfterSet: sourceExercise.restAfterSet,
          };
        } else {
          return {
            ...ex,
            isRepsSet: false as const,
            sets: sourceExercise.sets,
            restAfterSet: sourceExercise.restAfterSet,
          };
        }
      });

      setValue(`stepTwo.days.${dayNumber}.exercises`, updatedExercises, {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });

      toast.success('Dane ćwiczenia zostały skopiowane');
    },
    [formExercises, dayNumber, setValue],
  );

  const handleBack = useCallback(() => {
    const updatedDay = getValues(`stepTwo.days.${dayNumber}`);
    setValue(`stepTwo.days.${dayNumber}`, updatedDay, {
      shouldValidate: true,
      shouldDirty: true,
      shouldTouch: true,
    });
    onDaySelect(null);
  }, [dayNumber, getValues, onDaySelect, setValue]);

  const handleRestDaySwitch = useCallback(
    async (val: boolean) => {
      if (val && formExercises.length > 0) {
        alertDialog.open();
        return;
      }

      setValue(`stepTwo.days.${dayNumber}.isRestDay`, val, {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });

      if (val) {
        await trigger(`stepTwo.days.${dayNumber}.exercises`);
      }
    },
    [formExercises.length, alertDialog, dayNumber, trigger, setValue],
  );

  const handleAlertConfirm = useCallback(async () => {
    setValue(`stepTwo.days.${dayNumber}.exercises`, [], {
      shouldValidate: true,
      shouldDirty: true,
      shouldTouch: true,
    });
    setValue(`stepTwo.days.${dayNumber}.isRestDay`, true);
    await trigger(`stepTwo.days.${dayNumber}.exercises`);
    alertDialog.close();
  }, [dayNumber, setValue, trigger, alertDialog]);

  return {
    onExerciseRemove: handleExerciseRemove,
    onExerciseToggle: handleExerciseToggle,
    onReorder: handleReorder,
    onExerciseCopy: handleExerciseCopy,
    onBack: handleBack,
    onAlertConfirm: handleAlertConfirm,
    onRestDaySwitch: handleRestDaySwitch,
    isExerciseAdded,
    alertDialog,
    exerciseIds,
    isRestDay,
    formExercises,
    control,
    errors,
  };
}

export default useExercisesManager;
