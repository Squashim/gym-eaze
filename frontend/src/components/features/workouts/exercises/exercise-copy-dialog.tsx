import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { isDefaultExercise } from '@/lib/utils';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import type { WorkoutApiExercise } from '@/types/workout';

type ExerciseCopyDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  targetExerciseId: string;
  sourceExerciseId: string | null;
  orderedExercises: (GetExercisesByIdsApiResponse | WorkoutApiExercise)[];
  setTargetExerciseId: (val: string) => void;
  onCopy: () => void;
  onClose: () => void;
};

function ExerciseCopyDialog({
  open,
  onOpenChange,
  targetExerciseId,
  sourceExerciseId,
  orderedExercises,
  setTargetExerciseId,
  onCopy,
  onClose,
}: ExerciseCopyDialogProps) {
  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden">
        <DialogHeader>
          <DialogTitle>Kopiuj dane ćwiczenia</DialogTitle>
          <DialogDescription>
            Wybierz ćwiczenie docelowe, do którego chcesz skopiować dane. Spowoduje to nadpisanie
            danych w wybranym ćwiczeniu.
          </DialogDescription>
        </DialogHeader>
        <div className="overflow-hidden min-w-0 w-full">
          <Select onValueChange={setTargetExerciseId} value={targetExerciseId}>
            <SelectTrigger className="max-w-full w-full">
              <SelectValue placeholder="Wybierz ćwiczenie docelowe" />
            </SelectTrigger>
            <SelectContent className="w-[var(--radix-select-trigger-width)]">
              {orderedExercises
                .filter((ex) => {
                  const exerciseId = isDefaultExercise(ex) ? ex.originalId : ex._id;
                  return exerciseId !== sourceExerciseId;
                })
                .map((ex) => {
                  const exerciseId = isDefaultExercise(ex) ? ex.originalId : ex._id;

                  return (
                    <SelectItem key={exerciseId} value={exerciseId}>
                      {ex.name}
                    </SelectItem>
                  );
                })}
            </SelectContent>
          </Select>
        </div>

        <div className="flex items-center justify-end gap-2 mt-2">
          <Button
            onClick={onClose}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                e.preventDefault();
                onClose();
              }
            }}
            type="button"
            variant="outline"
          >
            Anuluj
          </Button>
          <Button disabled={!targetExerciseId} onClick={onCopy} type="button">
            Nadpisz ćwiczenie
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default ExerciseCopyDialog;
