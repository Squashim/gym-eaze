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
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectSeparator,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import Typography from '@/components/ui/typography';
import type { TrainingDaySchema } from '@/schemas/workout-schema';
import type { WorkoutWeek } from '@/types/workout';

type DayCopyDialogProps = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  days: TrainingDaySchema[];
  weeks: WorkoutWeek[];
  sourceDay: TrainingDaySchema | null;
  targetDayNumber: string;
  setTargetDayNumber: (val: string) => void;
  onCopy: () => void;
  onClose: () => void;
};

function DayCopyDialog({
  open,
  onOpenChange,
  days,
  weeks,
  sourceDay,
  targetDayNumber,
  setTargetDayNumber,
  onCopy,
  onClose,
}: DayCopyDialogProps) {
  const filteredDays = days.filter(
    (day) => day.dayNumber !== sourceDay?.dayNumber && day.name !== '' && !day.isRestDay,
  );

  const weekGroups = weeks.map((week) => ({
    name: week.name,
    days: week.days.filter((day) => filteredDays.some((fd) => fd.dayNumber === day.dayNumber)),
  }));

  const hasAnyDays = weekGroups.some((group) => group.days.length > 0);

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden">
        <DialogHeader>
          <DialogTitle>Wybierz dzień do którego chcesz skopiować dane</DialogTitle>
          <DialogDescription>
            Funkcja ta skopiuje wszystkie ćwiczenia z wybranego dnia do innego dnia. Pamiętaj, że
            spowoduje to nadpisanie wszystkich ćwiczeń w dniu docelowym.
          </DialogDescription>
        </DialogHeader>
        <div className="overflow-hidden min-w-0 w-full">
          <Select onValueChange={setTargetDayNumber} value={targetDayNumber}>
            <SelectTrigger className="max-w-full w-full">
              <SelectValue placeholder="Wybierz dzień docelowy" />
            </SelectTrigger>
            <SelectContent className="w-[var(--radix-select-trigger-width)]">
              {!hasAnyDays ? (
                <SelectGroup>
                  <SelectLabel>
                    Brak dostępnych dni do skopiowania. Dodaj więcej dni z ćwiczeniami.
                  </SelectLabel>
                </SelectGroup>
              ) : (
                weekGroups.map(
                  (group) =>
                    group.days.length > 0 && (
                      <SelectGroup key={group.name}>
                        <SelectLabel>{group.name}</SelectLabel>
                        {group.days.map((day) => (
                          <SelectItem key={day.dayNumber} value={day.dayNumber.toString()}>
                            <div className="flex items-center gap-1 ">
                              <Typography className="line-clamp-2">{day.name}</Typography>
                              <Typography className="text-xs" variant="muted">
                                #{day.dayNumber + 1}
                              </Typography>
                            </div>
                          </SelectItem>
                        ))}
                        {group.name !== weekGroups.at(-1)?.name && <SelectSeparator />}
                      </SelectGroup>
                    ),
                )
              )}
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
          <Button disabled={!targetDayNumber} onClick={onCopy} type="button">
            Nadpisz dzień
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default DayCopyDialog;
