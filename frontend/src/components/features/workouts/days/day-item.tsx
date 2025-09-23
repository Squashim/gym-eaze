import Typography from '@/components/ui/typography';
import { parseExerciseLabel } from '@/lib/parsers';
import { cn, getDayTimeRange } from '@/lib/utils';
import type { TrainingDaySchema } from '@/schemas/workout-schema';
import DayManageDropdown from './day-manage-dropdown';
import { useCallback } from 'react';

interface DayItemProps {
  day: TrainingDaySchema;
  onDialogOpen: (day: TrainingDaySchema) => void;
  onSelect: (day: TrainingDaySchema) => void;
  onDayDelete: (dayNumber: number) => void;
  daysCount: number;
  isExercisesError?: boolean;
  isDayNameError?: boolean;
}

function DayItem({
  day,
  daysCount,
  isDayNameError,
  isExercisesError,
  onDialogOpen,
  onSelect,
  onDayDelete,
}: DayItemProps) {
  const isEmpty = day.exercises.length === 0;

  const handleSelect = useCallback(() => {
    onSelect(day);
  }, [onSelect, day]);

  const handleDelete = useCallback(() => {
    onDayDelete(day.dayNumber);
  }, [onDayDelete, day.dayNumber]);

  const handleDialogOpen = useCallback(() => {
    onDialogOpen(day);
  }, [onDialogOpen, day]);

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        onSelect(day);
      }
    },
    [onSelect, day],
  );

  return (
    <div
      className={cn(
        'rounded-md p-3 shadow-md flex flex-col border hover:bg-primary/10 cursor-pointer transition-[background,border] ease-in-out duration-200 hover:border-primary/30 overflow-hidden',
        day.isRestDay && 'border-secondary hover:border-secondary/70',
        (isDayNameError || isExercisesError) && 'border-destructive hover:border-destructive/70',
      )}
      onClick={handleSelect}
      onKeyDown={handleKeyDown}
      role="button"
      tabIndex={0}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-baseline gap-1 w-full min-w-0">
          <Typography
            className={cn(
              'text-lg truncate max-w-full min-w-0',
              isDayNameError && 'text-destructive',
            )}
          >
            {day.name === '' ? 'Uzupełnij nazwę dnia' : day.name}
          </Typography>
          <Typography className="text-xs" variant="muted">
            {`#${day.dayNumber + 1}`}
          </Typography>
        </div>

        <DayManageDropdown
          daysCount={daysCount}
          exercisesCount={day.exercises.length}
          onDelete={handleDelete}
          onDialogOpen={handleDialogOpen}
        />
      </div>

      <Typography
        className={cn(day.isRestDay && 'text-secondary', isExercisesError && 'text-destructive')}
        variant="muted"
      >
        {day.isRestDay
          ? 'Regeneracja'
          : isEmpty
            ? 'Uzupełnij ćwiczenia • Brak danych'
            : `${parseExerciseLabel(day.exercises.length)} • ${getDayTimeRange(day)}`}
      </Typography>
    </div>
  );
}

export default DayItem;
