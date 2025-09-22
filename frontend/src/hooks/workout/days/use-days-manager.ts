import { getWeeks } from '@/lib/utils';
import type { WorkoutSchema } from '@/schemas/workout-schema';
import { useCallback, useMemo } from 'react';
import { useFormContext } from 'react-hook-form';
import { toast } from 'sonner';

function useDaysManager() {
  const {
    watch,
    setValue,
    trigger,
    formState: { errors },
  } = useFormContext<WorkoutSchema>();

  const days = watch('stepTwo.days');
  const weeks = useMemo(() => getWeeks(days), [days]);

  const handleTotalDaysChange = useCallback(
    (newTotal: number) => {
      if (newTotal > days.length) {
        const additionalDays = Array.from({ length: newTotal - days.length }, (_, i) => ({
          name: `Dzień ${days.length + i + 1}`,
          dayNumber: days.length + i,
          isRestDay: false,
          exercises: [],
        }));

        setValue('stepTwo.days', [...days, ...additionalDays], {
          shouldDirty: true,
          shouldTouch: true,
        });
      } else {
        setValue('stepTwo.days', days.slice(0, newTotal), {
          shouldDirty: true,
          shouldTouch: true,
        });
      }
    },
    [days, setValue],
  );

  const handleDayDelete = useCallback(
    (dayNumber: number) => {
      if (days.length === 1) return;

      const updatedDays = days
        .filter((day) => day.dayNumber !== dayNumber)
        .map((day, index) => ({ ...day, dayNumber: index }));

      setValue('stepTwo.days', updatedDays, { shouldDirty: true, shouldTouch: true });

      toast.success('Dzień został usunięty');
    },
    [days, setValue],
  );

  const handleDayCopy = useCallback(
    async (sourceDayNumber: number, targetDayNumber: number) => {
      const sourceDay = days.find((day) => day.dayNumber === sourceDayNumber);
      if (!sourceDay) {
        toast.error('Dzień źródłowy nie istnieje');
        return;
      }

      const updatedDays = days.map((day) =>
        day.dayNumber === targetDayNumber ? { ...day, exercises: [...sourceDay.exercises] } : day,
      );

      setValue('stepTwo.days', updatedDays, { shouldDirty: true, shouldTouch: true });
      await trigger(`stepTwo.days.${targetDayNumber}.exercises`);

      toast.success('Ćwiczenia zostały skopiowane');
    },
    [days, setValue, trigger],
  );

  return {
    days,
    weeks,
    errors,
    onTotalDaysChange: handleTotalDaysChange,
    onDayDelete: handleDayDelete,
    onDayCopy: handleDayCopy,
  };
}

export default useDaysManager;
