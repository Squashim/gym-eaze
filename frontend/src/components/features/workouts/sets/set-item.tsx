import { Separator } from '@/components/ui/separator';
import Typography from '@/components/ui/typography';
import { parseSecondsToTimeString } from '@/lib/parsers';
import type { WorkoutSchema } from '@/schemas/workout-schema';
import { useFormContext } from 'react-hook-form';
import SetManageDropdown from './set-manage-dropdown';
import { FormControl, FormField, FormItem, FormLabel } from '@/components/ui/form';
import NumberInput from '@/components/ui/number-input';
import { WORKOUT } from '@/constants/workout';
import { useCallback } from 'react';

interface SetItemProps {
  setNumber: number;
  setsCount: number;
  dayNumber: number;
  exerciseNumber: number;
  isRepsSet: boolean;
  isLastExercise: boolean;
  onDuplicate: (setNumber: number) => void;
  onDelete: (setNumber: number) => void;
}

const SetItem = ({
  setNumber,
  setsCount,
  dayNumber,
  exerciseNumber,
  isRepsSet,
  isLastExercise,
  onDuplicate,
  onDelete,
}: SetItemProps) => {
  const { control, watch } = useFormContext<WorkoutSchema>();
  const restAfterSet = watch(`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.restAfterSet`);

  const handleDelete = useCallback(() => {
    onDelete(setNumber);
  }, [onDelete, setNumber]);

  const handleDuplicate = useCallback(() => {
    onDuplicate(setNumber);
  }, [onDuplicate, setNumber]);

  return (
    <>
      <div className="rounded-md p-3 shadow-md flex flex-col border gap-2">
        <div className="flex items-center justify-between">
          <Typography className="text-lg">{`Seria ${setNumber + 1}`}</Typography>
          <SetManageDropdown
            onDelete={handleDelete}
            onDuplicate={handleDuplicate}
            setsCount={setsCount}
          />
        </div>

        {isRepsSet ? (
          <div className="flex items-center gap-4 justify-between w-full">
            <FormField
              control={control}
              name={`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets.${setNumber}.repetitions`}
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor={field.name}>
                    Liczba powtórzeń
                  </FormLabel>
                  <FormControl>
                    <NumberInput
                      id={field.name}
                      max={WORKOUT.MAX_REPS_PER_SET}
                      min={1}
                      onChange={field.onChange}
                      step={1}
                      value={field.value}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            <FormField
              control={control}
              name={`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets.${setNumber}.weight`}
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold justify-end" htmlFor={field.name}>
                    Obciążenie
                  </FormLabel>
                  <FormControl>
                    <NumberInput
                      id={field.name}
                      max={WORKOUT.MAX_WEIGHT}
                      min={0}
                      onChange={field.onChange}
                      placeholder="Brak"
                      step={5}
                      suffix="kg"
                      value={field.value ?? 0}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
          </div>
        ) : (
          <FormField
            control={control}
            name={`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.sets.${setNumber}.duration`}
            render={({ field }) => (
              <FormItem>
                <FormLabel className="font-semibold" htmlFor={field.name}>
                  Czas trwania serii
                </FormLabel>
                <FormControl>
                  <NumberInput
                    id={field.name}
                    max={WORKOUT.MAX_DURATION_PER_SET}
                    min={WORKOUT.DEFAULT_SETS_DURATION}
                    onChange={field.onChange}
                    step={5}
                    suffix="s"
                    value={field.value}
                  />
                </FormControl>
              </FormItem>
            )}
          />
        )}
      </div>

      {(setNumber < setsCount - 1 || !isLastExercise) && (
        <RestAfterSetSeparator restTime={restAfterSet} />
      )}
    </>
  );
};

function RestAfterSetSeparator({ restTime }: { restTime: number }) {
  const parsedText = parseSecondsToTimeString(restTime);

  return (
    <div className="flex items-center">
      <Separator className="flex-1 bg-secondary" />
      <Typography className="px-3 text-secondary text-sm">{parsedText}</Typography>
      <Separator className="flex-1 bg-secondary" />
    </div>
  );
}

export default SetItem;
