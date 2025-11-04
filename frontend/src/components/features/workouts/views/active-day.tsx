import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import useOpen from '@/hooks/utils/use-open';
import type { TrainingDaySchema } from '@/schemas/workout-schema';
import { useState } from 'react';
import ExercisesSheet from '../exercises/sheet/sheet';
import { Button } from '@/components/ui/button';
import { Info, Plus, TriangleAlert } from 'lucide-react';
import Typography from '@/components/ui/typography';
import BackHeader from '@/components/ui/back-header';
import {
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { WORKOUT } from '@/constants/workout';
import { Separator } from '@/components/ui/separator';
import ActiveExercise from './active-exercise';
import useExercisesManager from '@/hooks/workout/exercises/use-exercises-manager';
import ExercisesList from '../exercises/exercises-list';
import { Alert, AlertTitle } from '@/components/ui/alert';
import type { WorkoutApiExercise } from '@/types/workout';

interface ActiveDayViewProps {
  day: TrainingDaySchema;
  onDaySelect: (day: TrainingDaySchema | null) => void;
  defaultExercisesMap?: Map<string, WorkoutApiExercise>;
}

const ActiveDay = ({ day, onDaySelect, defaultExercisesMap }: ActiveDayViewProps) => {
  const exercisesSheet = useOpen();

  const {
    onExerciseRemove,
    onExerciseToggle,
    onReorder,
    onExerciseCopy,
    onBack,
    onAlertConfirm,
    onRestDaySwitch,
    isExerciseAdded,
    alertDialog,
    isRestDay,
    formExercises,
    exerciseIds,
    control,
    errors,
  } = useExercisesManager({ dayNumber: day.dayNumber, onDaySelect });

  const [selectedExerciseId, setSelectedExerciseId] = useState<string | null>(null);

  if (selectedExerciseId) {
    return (
      <ActiveExercise
        dayNumber={day.dayNumber}
        defaultExercisesMap={defaultExercisesMap}
        exerciseId={selectedExerciseId}
        exerciseIds={exerciseIds}
        onExerciseSelect={setSelectedExerciseId}
      />
    );
  }

  return (
    <>
      <div className="md:col-span-3 flex flex-col gap-2">
        <BackHeader onBack={onBack} title="Edytujesz dzień treningowy" />

        <FormField
          control={control}
          name={`stepTwo.days.${day.dayNumber}.name`}
          render={({ field }) => (
            <FormItem>
              <FormLabel className="font-semibold">Nazwa dnia</FormLabel>
              <FormControl>
                <Input
                  {...field}
                  autoComplete="off"
                  autoFocus
                  maxLength={100}
                  placeholder="Podaj nazwę dnia treningowego"
                  type="text"
                />
              </FormControl>
              <FormMessage />
              <FormDescription>Maksymalna długość nazwy to 100 znaków.</FormDescription>
            </FormItem>
          )}
        />

        <FormField
          control={control}
          name={`stepTwo.days.${day.dayNumber}.isRestDay`}
          render={({ field }) => (
            <FormItem className="flex flex-row items-center justify-between rounded-md border p-3 md:col-span-2">
              <div className="flex flex-col gap-1">
                <FormLabel className="font-semibold">Dzień wolny od ćwiczeń</FormLabel>
                <FormDescription>
                  Ustawienie dnia jako wolny, zablokuje możliwość dodawania do niego ćwiczeń.
                </FormDescription>
              </div>
              <FormControl>
                <Switch checked={field.value} name={field.name} onCheckedChange={onRestDaySwitch} />
              </FormControl>
            </FormItem>
          )}
        />

        <Button
          disabled={isRestDay || formExercises.length >= WORKOUT.MAX_EXERCISES_PER_DAY}
          onClick={exercisesSheet.open}
          type="button"
        >
          <Plus />
          Dodaj ćwiczenia
        </Button>

        <Separator className="mt-2" />

        {!isRestDay && (
          <div className="flex flex-col">
            <Typography className="text-lg" variant="h4">
              Kliknij ćwiczenie, aby je edytować
            </Typography>
            <Typography variant="muted">Dodano ({formExercises.length}/15) ćwiczeń</Typography>
          </div>
        )}

        {errors.stepTwo?.days?.[day.dayNumber]?.exercises && (
          <div className="w-full bg-destructive/10 flex items-center gap-2 border border-destructive rounded-md  text-left p-2">
            <TriangleAlert className="text-destructive" size={18} />
            <Typography className="text-destructive" variant="small">
              {errors.stepTwo?.days?.[day.dayNumber]?.exercises?.message}
            </Typography>
          </div>
        )}

        <div className="grid gap-3 overflow-y-auto scrollbar focus-within:outline-none p-0.5 max-h-[40vh]">
          {isRestDay ? (
            <Alert>
              <Info />
              <AlertTitle>Odpocznij! Dzień wolny nie zawiera ćwiczeń</AlertTitle>
            </Alert>
          ) : formExercises.length === 0 ? (
            <Alert>
              <Info />
              <AlertTitle>Brak dodanych ćwiczeń</AlertTitle>
            </Alert>
          ) : (
            <ExercisesList
              defaultExercisesMap={defaultExercisesMap}
              exerciseIds={exerciseIds}
              formExercises={formExercises}
              onExerciseCopy={onExerciseCopy}
              onExerciseRemove={onExerciseRemove}
              onExerciseSelect={setSelectedExerciseId}
              onReorder={onReorder}
            />
          )}
        </div>
      </div>

      <ExercisesSheet
        isExerciseAdded={isExerciseAdded}
        isOpen={exercisesSheet.isOpen}
        onExerciseToggle={onExerciseToggle}
        setIsOpen={exercisesSheet.setIsOpen}
      />

      <ReusableAlertDialog
        cancelLabel="Anuluj"
        confirmLabel="Potwierdź"
        description="Ustawienie dnia jako wolnego spowoduje usunięcie wszystkich ćwiczeń dodanych do tego dnia. Czy na pewno chcesz to zrobić?"
        onClose={alertDialog.close}
        onConfirm={onAlertConfirm}
        open={alertDialog.isOpen}
        title="Potwierdź ustawienie dnia jako wolnego"
      />
    </>
  );
};

export default ActiveDay;
