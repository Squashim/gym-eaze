import NumberInput from '@/components/ui/number-input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import Typography from '@/components/ui/typography';
import { cn } from '@/lib/utils';
import type { TrainingDaySchema } from '@/schemas/workout-schema';

type DaysListViewProps = {
  totalDays: number;
  handleTotalDaysChange: (days: number) => void;
  weeks: { name: string; days: TrainingDaySchema[] }[];
  setActiveDayIndex: (index: number | null) => void;
};

function DaysList({
  totalDays,
  handleTotalDaysChange,
  weeks,
  setActiveDayIndex,
}: DaysListViewProps) {
  return (
    <div className="md:col-span-3 flex flex-col gap-4">
      <div className="self-center">
        <NumberInput
          className="w-32"
          label="Podaj liczbę dni"
          max={30}
          min={1}
          onChange={handleTotalDaysChange}
          placeholder="30"
          value={totalDays}
        />
      </div>

      <Tabs defaultValue="week-1">
        <div className="relative overflow-x-auto h-16 pt-2 scrollbar border-b">
          <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2">
            {weeks.map((week, i) => (
              <TabsTrigger className="h-10 px-4" key={i} value={`week-${i + 1}`}>
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
                {week.days.map((day, j) => (
                  <DayItem day={day} key={j} onClick={() => setActiveDayIndex(j + i * 7)} />
                ))}
              </div>
            </TabsContent>
          ))}
        </div>
      </Tabs>
    </div>
  );
}

interface DayItemProps {
  day: TrainingDaySchema;
  onClick: () => void;
}

function DayItem({ day, onClick }: DayItemProps) {
  const isEmpty = day.exercises.length === 0;
  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      onClick();
    }
  };

  return (
    <div
      className={cn(
        'rounded-md p-3 shadow-md flex flex-col border hover:bg-primary/10 cursor-pointer transition-[background,border] ease-in-out duration-200 hover:border-primary/30',
        day.isRestDay && 'border-secondary hover:border-secondary/70',
      )}
      onClick={onClick}
      onKeyDown={(e) => handleKeyDown(e)}
      role="button"
      tabIndex={0}
    >
      <Typography className="text-lg">{day.name}</Typography>
      <Typography className={cn(day.isRestDay && 'text-secondary')} variant="muted">
        {day.isRestDay
          ? 'Regeneracja'
          : isEmpty
            ? 'Uzupełnij ćwiczenia • Brak danych'
            : 'ćwiczenia'}
      </Typography>
    </div>
  );
}

export default DaysList;
