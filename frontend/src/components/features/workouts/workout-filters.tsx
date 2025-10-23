import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import SearchInput from '@/components/ui/search-input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';
import { Slider } from '@/components/ui/slider';
import { Switch } from '@/components/ui/switch';
import { TagsInput } from '@/components/ui/tags-input';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Goal } from '@/constants/enums/goals';
import { WORKOUT } from '@/constants/workout';
import { useUser } from '@/context/user';
import useDebounce from '@/hooks/utils/use-debounce';
import { parseDayLabel } from '@/lib/parsers';
import { cn } from '@/lib/utils';
import type { WorkoutFilters as Filters, SortBy } from '@/types/filters';
import {
  ArrowUpDown,
  BicepsFlexed,
  Bike,
  Compass,
  Droplets,
  Dumbbell,
  Filter,
  Footprints,
  HeartPulse,
  Pizza,
  Shuffle,
  Timer,
  Worm,
  Wrench,
  X,
  type LucideIcon,
} from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';

interface WorkoutFiltersProps {
  filters: Filters;
  sortBy: SortBy;
  setFilters: (filters: Partial<Filters>) => void;
  clearFilters: () => void;
  setSortBy: (sortBy: SortBy) => void;
}

function WorkoutFilters({
  filters,
  sortBy,
  setSortBy,
  setFilters,
  clearFilters,
}: WorkoutFiltersProps) {
  const { user } = useUser();

  const [openFilterSheet, setOpenFilterSheet] = useState(false);
  const [searchText, setSearchText] = useState(filters.search ?? '');
  const [sliderValues, setSliderValues] = useState([
    filters.minDuration ?? 1,
    filters.maxDuration ?? WORKOUT.MAX_DAYS,
  ]);
  const debouncedSearch = useDebounce(searchText, 350);

  const activeFiltersCount = useMemo(() => {
    let durationActive = false;

    const count = Object.entries(filters).reduce((acc, [key, value]) => {
      if (key === 'search') return acc;
      if (key === 'minDuration' || key === 'maxDuration') {
        if (
          (key === 'minDuration' && value && value !== 1) ||
          (key === 'maxDuration' && value && value !== WORKOUT.MAX_DAYS)
        ) {
          durationActive = true;
        }
        return acc;
      }
      if (Array.isArray(value) && value.length > 0) return acc + 1;
      if (typeof value === 'boolean' && value) return acc + 1;
      if (typeof value === 'string' && value !== '') return acc + 1;
      return acc;
    }, 0);

    return count + (durationActive ? 1 : 0);
  }, [filters]);

  useEffect(() => {
    setFilters({ search: debouncedSearch });
  }, [debouncedSearch, setFilters]);

  const handleSearchClear = () => {
    setSearchText('');
    setFilters({ search: '' });
  };

  const handleFiltersClear = () => {
    clearFilters();
    setSliderValues([1, WORKOUT.MAX_DAYS]);
  };

  const handleAdvancedLevelChange = (value: AdvancedLevel) => {
    if (value === AdvancedLevel.none) {
      setFilters({ difficulty: undefined });
    } else {
      setFilters({ difficulty: value });
    }
  };

  const handleGoalChange = (value: Goal) => {
    if (value === Goal.none) {
      setFilters({ goal: undefined });
    } else {
      setFilters({ goal: value });
    }
  };

  const renderFilterSheet = () => {
    return (
      <Sheet onOpenChange={(open) => setOpenFilterSheet(open)} open={openFilterSheet}>
        <SheetContent className="overflow-y-auto scrollbar [&>button:first-of-type]:hidden">
          <SheetHeader>
            <SheetTitle className="scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight first:mt-0">
              Filtry
            </SheetTitle>
            <SheetClose asChild>
              <Button className="absolute right-4 top-4" size="icon" variant="outline">
                <X className="size-5" />
              </Button>
            </SheetClose>
            <SheetDescription className="text-accent-foreground/80">
              Wybierz filtry, aby zawęzić wyniki wyszukiwania.
            </SheetDescription>
          </SheetHeader>
          <div className="flex flex-col gap-8 p-4">
            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Cel treningowy:
              </Typography>
              <Select onValueChange={handleGoalChange} value={filters.goal ?? Goal.none}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Wybierz cel treningowy..." />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(Goal).map(([key, value]) => (
                    <SelectItem key={key} value={value}>
                      {value}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Poziom trudności:
              </Typography>
              <Select
                onValueChange={handleAdvancedLevelChange}
                value={filters.difficulty ?? AdvancedLevel.none}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Wybierz poziom trudności..." />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(AdvancedLevel).map(([key, value]) => (
                    <SelectItem key={key} value={value}>
                      {value}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Długość planu:
              </Typography>
              <div className="flex flex-col gap-2">
                <Slider
                  max={WORKOUT.MAX_DAYS}
                  min={1}
                  onValueChange={(values) => {
                    setSliderValues(values);
                    setFilters({
                      minDuration: values[0],
                      maxDuration: values[1],
                    });
                  }}
                  step={1}
                  value={sliderValues}
                />
                <div className="w-full flex items-center justify-between">
                  <Badge className="min-h-7">{parseDayLabel(sliderValues[0])}</Badge>
                  <Badge className="min-h-7">{parseDayLabel(sliderValues[1])}</Badge>
                </div>
              </div>
            </div>

            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Hasztagi:
              </Typography>
              <TagsInput
                id="exercise-filter-hashtags"
                onChange={(tags) => setFilters({ hashtags: tags })}
                placeholder="Podaj hasztagi, np. siła, sztanga - użyj Entera lub przecinka"
                value={filters.hashtags || []}
              />
            </div>
            {!!user && (
              <div className="grid gap-2">
                <Typography className="font-semibold" variant="small">
                  Pokaż plany obserwowanych użytkowników:
                </Typography>
                <div className="flex flex-row items-center justify-between rounded-md border border-input p-3 bg-transparent dark:bg-input/30 shadow-xs">
                  <div className="grid gap-1">
                    <Typography variant="muted">
                      Włącz, aby wyświetlać tylko plany treningowe osób, które obserwujesz
                    </Typography>
                  </div>
                  <Switch
                    checked={filters.followers === true}
                    onCheckedChange={(checked) =>
                      setFilters({ followers: checked ? true : undefined })
                    }
                  />
                </div>
              </div>
            )}
          </div>
        </SheetContent>
      </Sheet>
    );
  };

  return (
    <>
      <div className="flex flex-col gap-4">
        <div className="flex flex-col md:flex-row gap-4 md:gap-2 md:items-end justify-center w-full">
          <SearchInput
            className="max-w-lg"
            maxLength={64}
            onChange={(newValue) => setSearchText(newValue)}
            onClear={handleSearchClear}
            placeholder="Wyszukaj plan..."
            value={searchText}
          />

          <div className="flex gap-2">
            <Button className="relative" onClick={() => setOpenFilterSheet(true)}>
              <Filter />
              Więcej filtrów
              {activeFiltersCount > 0 && (
                <Badge
                  className="absolute -top-1 -right-1 rounded-full min-w-5 h-5 px-1 tabular-nums"
                  variant="secondary"
                >
                  {activeFiltersCount}
                </Badge>
              )}
            </Button>
            <Button
              className={cn(activeFiltersCount > 0 ? 'visible' : 'invisible')}
              onClick={handleFiltersClear}
              variant="ghost"
            >
              Wyczyść
            </Button>
          </div>

          <div className="grid gap-2 md:ml-auto">
            <Typography className="font-semibold" variant="small">
              Sortuj według:
            </Typography>
            <Select onValueChange={(value) => setSortBy(value as SortBy)} value={sortBy}>
              <SelectTrigger className="w-52">
                <div className="flex items-center gap-2">
                  <ArrowUpDown />
                  <SelectValue />
                </div>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="latest">Najnowsze</SelectItem>
                <SelectItem value="popular">Popularne</SelectItem>
                <SelectItem value="top-rated">Najlepiej oceniane</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <HorizontalScrollFilter filters={filters} setFilters={setFilters} />
      </div>
      {renderFilterSheet()}
    </>
  );
}

interface HorizontalScrollFilterProps {
  filters: Filters;
  setFilters: (filters: Partial<Filters>) => void;
}

function HorizontalScrollFilter({ filters, setFilters }: HorizontalScrollFilterProps) {
  const activeGoal = useMemo(() => {
    return filters.goal;
  }, [filters.goal]);

  const handleGoalSelect = useCallback(
    (newGoal: Goal) => {
      if (newGoal === activeGoal) {
        setFilters({ goal: undefined });
      } else {
        setFilters({ goal: newGoal });
      }
    },
    [setFilters, activeGoal],
  );

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent, goal: Goal) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        handleGoalSelect(goal);
      }
    },
    [handleGoalSelect],
  );

  const GoalIcons: Record<Exclude<keyof typeof Goal, 'none'>, LucideIcon> = {
    recovery: HeartPulse,
    strength: Dumbbell,
    posture: Wrench,
    massGain: Pizza,
    coordination: Compass,
    calorieBurn: Bike,
    mobility: Shuffle,
    legsGlutes: Footprints,
    endurance: Timer,
    fatLoss: Droplets,
    muscleGain: BicepsFlexed,
    flexibility: Worm,
  };

  const renderGoal = (key: Exclude<keyof typeof Goal, 'none'>, value: Goal) => {
    const isActive = activeGoal === value;
    const IconComponent = GoalIcons[key];

    return (
      <div
        aria-label={`${isActive ? 'Usuń' : 'Dodaj'} cel treningowy: ${value}`}
        aria-pressed={isActive}
        className={cn(
          'flex items-center p-4 gap-2 sm:min-h-20 shrink-0 hover:bg-primary/10 border-2 rounded-lg cursor-pointer transition-[background,border] ease-in-out duration-200 bg-input/30',
          isActive && 'border-secondary bg-primary/10 hover:bg-primary/20',
        )}
        key={key}
        onClick={() => handleGoalSelect(value)}
        onKeyDown={(e) => handleKeyDown(e, value)}
        role="button"
        tabIndex={0}
      >
        <IconComponent
          className={cn('size-8 text-muted-foreground stroke-[1.5]', isActive && 'text-secondary')}
        />
        <Typography
          aria-hidden="true"
          className={cn(
            'text-center text-lg text-muted-foreground leading-tight',
            isActive && 'text-secondary',
          )}
          variant="p"
        >
          {value}
        </Typography>
      </div>
    );
  };

  return (
    <div>
      <Typography variant="h3">Szukaj według celu treningowego</Typography>

      <div className="flex overflow-x-auto scrollbar gap-2 py-2 focus-within:outline-none">
        {Object.entries(Goal)
          .filter(([key]) => key !== 'none')
          .map(([key, value]) => renderGoal(key as Exclude<keyof typeof Goal, 'none'>, value))}
      </div>
    </div>
  );
}

export default WorkoutFilters;
