import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import useGetExercise from '@/hooks/query/exercises/use-get-exercise';
import { useMemo } from 'react';
import BackHeader from '../back-header';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';
import { AdvancedLevelBadge, LikesBadge } from '@/components/ui/item-info-badges';
import { Button } from '@/components/ui/button';
import { FormControl, FormDescription, FormField, FormItem, FormLabel } from '@/components/ui/form';
import { Switch } from '@/components/ui/switch';
import SetsList from '../sets/sets-list';
import { DetailsActionBtn } from '@/components/ui/reusable-buttons';
import useActiveExerciseManager from '@/hooks/workout/sets/use-active-exercise-manager';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Info } from 'lucide-react';

interface ActiveExerciseProps {
  exerciseId: string;
  exerciseIds: string[];
  dayNumber: number;
  onExerciseSelect: (id: string | null) => void;
}

const ActiveExercise = ({
  onExerciseSelect,
  exerciseId,
  exerciseIds,
  dayNumber,
}: ActiveExerciseProps) => {
  const { data: exercise, isLoading, isError } = useGetExercise(exerciseId);

  const {
    alertDialog,
    control,
    sets,
    isRepsSet,
    isLastExercise,
    exerciseNumber,
    onSwitchChange,
    onAlertConfirm,
    onBack,
  } = useActiveExerciseManager({
    exerciseIds,
    exerciseId,
    dayNumber,
    onExerciseSelect,
  });

  const displayMuscleGroups = useMemo(() => {
    if (!exercise) return '';
    return exercise.muscleGroup.map((mg) => mg.name).join(' • ');
  }, [exercise]);

  return (
    <>
      <div className="md:col-span-3 flex flex-col gap-2">
        <BackHeader onBack={onBack} title="Edytujesz ćwiczenie w dniu" />

        {isLoading && <Skeleton className="h-30" />}

        {isError && (
          <Alert>
            <Info />
            <AlertTitle>Wystąpił błąd podczas pobierania ćwiczenia.</AlertTitle>
          </Alert>
        )}

        {exercise && (
          <div className="rounded-md p-3 shadow-md flex items-center gap-2 border min-[425px]:h-30 bg-card text-card-foreground flex-wrap min-[425px]:flex-nowrap">
            <div className="flex-shrink-0 overflow-hidden h-full aspect-square max-h-30">
              <img
                alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
                className="w-full h-full aspect-square object-cover rounded-md bg-primary/5 dark:grayscale-[0.2] dark:contrast-110"
                src={exercise.image}
              />
            </div>

            <div className="flex flex-col items-start justify-between min-[425px]:h-full">
              <Typography className="text-sm font-semibold line-clamp-1">
                {exercise.name}
              </Typography>
              <Typography className="text-sm line-clamp-1" variant="muted">
                {displayMuscleGroups}
              </Typography>
              <div className="flex gap-2 items-center">
                <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
                <LikesBadge likes={exercise.likes} />
              </div>
              <Button asChild className="p-0 text-xs h-auto text-muted-foreground" variant="link">
                <a href={`/profile/${exercise.author._id}`} rel="noreferrer" target="_blank">
                  Autor: {exercise.author.username}
                </a>
              </Button>
            </div>

            <div className="sm:ml-auto max-[425px]:w-full">
              <DetailsActionBtn
                className="max-[425px]:w-full"
                redirectTo={`/exercises/${exercise._id}`}
              />
            </div>
          </div>
        )}

        <FormField
          control={control}
          name={`stepTwo.days.${dayNumber}.exercises.${exerciseNumber}.isRepsSet`}
          render={({ field }) => (
            <FormItem className="flex flex-row items-center justify-between rounded-md border p-3 md:col-span-2">
              <div className="flex flex-col gap-1">
                <FormLabel className="font-semibold">Seria czasowa/z powtórzeniami</FormLabel>
                <FormDescription>
                  Wybierz czy serie w tym ćwiczeniu mają być liczone na czas czy na powtórzenia.
                </FormDescription>
              </div>
              <FormControl>
                <Switch checked={field.value} name={field.name} onCheckedChange={onSwitchChange} />
              </FormControl>
            </FormItem>
          )}
        />

        <SetsList
          dayNumber={dayNumber}
          exerciseNumber={exerciseNumber}
          isLastExercise={isLastExercise}
          isRepsSet={isRepsSet}
          sets={sets}
        />
      </div>

      <ReusableAlertDialog
        cancelLabel="Anuluj"
        confirmLabel="Potwierdź"
        description={`Ustawienie serii jako ${isRepsSet ? 'czasowej' : 'powtórzeniowej'} spowoduje usunięcie wszystkich aktualnych serii. Czy na pewno chcesz to zrobić?`}
        onClose={alertDialog.close}
        onConfirm={onAlertConfirm}
        open={alertDialog.isOpen}
        title={
          isRepsSet
            ? 'Potwierdź ustawienie serii jako czasowej'
            : 'Potwierdź ustawienie serii jako powtórzeniowej'
        }
      />
    </>
  );
};

export default ActiveExercise;
