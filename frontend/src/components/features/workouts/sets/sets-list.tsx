import { FormControl, FormField, FormItem, FormLabel } from '@/components/ui/form';
import NumberInput from '@/components/ui/number-input';
import { WORKOUT } from '@/constants/workout';
import useSetsManager from '@/hooks/workout/sets/use-sets-manager';
import type { WorkoutSets } from '@/types/workout';
import SetItem from './set-item';

interface SetsListProps {
  sets: WorkoutSets[];
  dayNumber: number;
  exerciseNumber: number;
  isRepsSet: boolean;
}

const SetsList = ({ dayNumber, exerciseNumber, sets, isRepsSet }: SetsListProps) => {
  const { control, onTotalSetsChange, onDuplicate, onDelete } = useSetsManager({
    dayNumber,
    exerciseNumber,
    sets,
    isRepsSet,
  });

  return (
    <>
      <header className="flex items-center gap-4 justify-between py-4">
        <NumberInput
          autoFocus
          className="w-12"
          label="Podaj liczbę serii"
          labelClassName="font-semibold justify-start"
          max={WORKOUT.MAX_EXERCISES_PER_DAY}
          min={1}
          onChange={onTotalSetsChange}
          value={sets.length}
        />

        <FormField
          control={control}
          name={`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.restAfterSet`}
          render={({ field }) => (
            <FormItem>
              <FormLabel className="font-semibold justify-end" htmlFor={field.name}>
                Przerwa między seriami
              </FormLabel>
              <FormControl>
                <NumberInput
                  id={field.name}
                  max={WORKOUT.MAX_REST_AFTER_SET}
                  min={WORKOUT.DEFAULT_REST_AFTER_SET}
                  onChange={field.onChange}
                  step={5}
                  suffix="s"
                  value={field.value}
                />
              </FormControl>
            </FormItem>
          )}
        />
      </header>

      <div className="flex flex-col gap-2 overflow-y-auto scrollbar max-h-[40dvh]">
        {sets.map((_, i) => (
          <SetItem
            dayNumber={dayNumber}
            exerciseNumber={exerciseNumber}
            isRepsSet={isRepsSet}
            key={i}
            onDelete={onDelete}
            onDuplicate={onDuplicate}
            setNumber={i}
            setsCount={sets.length}
          />
        ))}
      </div>
    </>
  );
};

export default SetsList;
