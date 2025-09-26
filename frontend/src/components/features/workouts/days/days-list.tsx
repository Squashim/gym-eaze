import NumberInput from '@/components/ui/number-input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import Typography from '@/components/ui/typography';
import { WORKOUT } from '@/constants/workout';
import useDaysManager from '@/hooks/workout/days/use-days-manager';
import type { TrainingDaySchema } from '@/schemas/workout-schema';
import DayItem from './day-item';
import useDayCopyDialog from '@/hooks/workout/days/use-day-copy-dialog';
import DayCopyDialog from './day-copy-dialog';
import { useCallback } from 'react';

type DaysListViewProps = {
  onDaySelect: (day: TrainingDaySchema | null) => void;
  onTabSelect: (tab: string) => void;
  selectedTab: string;
};

function DaysList({ onDaySelect, onTabSelect, selectedTab }: DaysListViewProps) {
  const { days, weeks, errors, onTotalDaysChange, onDayDelete, onDayCopy } = useDaysManager();

  const {
    dialog,
    sourceDay,
    targetDayNumber,
    setTargetDayNumber,
    onOpenDialog,
    onCloseDialog,
    onConfirmCopy,
  } = useDayCopyDialog(onDayCopy);

  const handleDialogOpenChange = useCallback(
    (open: boolean) => {
      if (open) {
        dialog.open();
      } else {
        onCloseDialog();
      }
    },
    [dialog, onCloseDialog],
  );

  return (
    <>
      <div className="md:col-span-3 flex flex-col gap-4">
        <div className="self-center">
          <NumberInput
            className="w-32"
            label="Podaj liczbę dni"
            max={WORKOUT.MAX_DAYS}
            min={1}
            onChange={onTotalDaysChange}
            value={days.length}
          />
        </div>

        <Tabs defaultValue={selectedTab} onValueChange={onTabSelect}>
          <div className="relative overflow-x-auto h-16 pt-2 scrollbar border-b">
            <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2">
              {weeks.map((week, i) => (
                <TabsTrigger className="h-10 px-4" key={week.name} value={`week-${i + 1}`}>
                  {week.name}
                </TabsTrigger>
              ))}
            </TabsList>
          </div>

          <div className="flex flex-col gap-2">
            <Typography className="text-lg" variant="h4">
              Edytuj wybrany dzień poprzez jego kliknięcie
            </Typography>
            {weeks.map((week, i) => (
              <TabsContent key={i} value={`week-${i + 1}`}>
                <div className="grid gap-3 overflow-y-auto scrollbar max-h-[40dvh] focus-within:outline-none p-0.5">
                  {week.days.map((day) => (
                    <DayItem
                      day={day}
                      daysCount={days.length}
                      isDayNameError={!!errors.stepTwo?.days?.[day.dayNumber]?.name}
                      isExercisesError={!!errors.stepTwo?.days?.[day.dayNumber]?.exercises}
                      key={day.dayNumber}
                      onDayDelete={onDayDelete}
                      onDialogOpen={onOpenDialog}
                      onSelect={onDaySelect}
                    />
                  ))}
                </div>
              </TabsContent>
            ))}
          </div>
        </Tabs>
      </div>

      <DayCopyDialog
        days={days}
        onClose={onCloseDialog}
        onCopy={onConfirmCopy}
        onOpenChange={handleDialogOpenChange}
        open={dialog.isOpen}
        setTargetDayNumber={setTargetDayNumber}
        sourceDay={sourceDay}
        targetDayNumber={targetDayNumber}
        weeks={weeks}
      />
    </>
  );
}

export default DaysList;
