import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { MultiSelect } from '@/components/ui/multi-select';
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { TagsInput } from '@/components/ui/tags-input';
import Typography from '@/components/ui/typography';
import useGetEquipment from '@/hooks/query/equipment/use-get-equipment';
import useGetMuscleGroups from '@/hooks/query/muscle-groups/use-get-muscle-groups';
import useDebounce from '@/hooks/use-debounce';
import { apiResponseToMultiSelectOptions, cn } from '@/lib/utils';
import type { ExerciseFilters as Filters, SortBy } from '@/types/filters';
import { ArrowUpDown, Filter, Search, X } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { AdvancedLevel } from '@/constants/advanced-level';
import { Switch } from '@/components/ui/switch';
import { Badge } from '@/components/ui/badge';
import type { MuscleGroup } from '@/types/muscle-group';
import type { Equipment } from '@/types/equipment';
import { Skeleton } from '@/components/ui/skeleton';

type ExerciseFiltersProps = {
  filters: Filters;
  sortBy: SortBy;
  setFilters: (filters: Partial<Filters>) => void;
  clearFilters: () => void;
  setSortBy: (sortBy: SortBy) => void;
};

const ExerciseFilters = ({
  filters,
  sortBy,
  setSortBy,
  setFilters,
  clearFilters,
}: ExerciseFiltersProps) => {
  const [openFilterSheet, setOpenFilterSheet] = useState(false);
  const [searchText, setSearchText] = useState(filters.search ?? '');
  const [activeFiltersCount, setActiveFiltersCount] = useState(0);
  const debouncedSearch = useDebounce(searchText, 350);
  const {
    data: muscleGroups,
    isLoading: isMuscleGroupsLoading,
    isError: isMuscleGroupError,
  } = useGetMuscleGroups();
  const {
    data: equipment,
    isLoading: isEquipmentLoading,
    isError: isEquipmentError,
  } = useGetEquipment();

  useEffect(() => {
    const count = Object.entries(filters).reduce((acc, [key, value]) => {
      if (key === 'search') return acc;
      if (Array.isArray(value) && value.length > 0) return acc + 1;
      if (typeof value === 'boolean' && value) return acc + 1;
      if (typeof value === 'string' && value !== '') return acc + 1;
      return acc;
    }, 0);

    setActiveFiltersCount(count);
  }, [filters]);

  useEffect(() => {
    setFilters({ search: debouncedSearch });
  }, [debouncedSearch, setFilters]);

  const handleSearchClear = () => {
    setSearchText('');
    setFilters({ search: '' });
  };

  const handleAdvancedLevelChange = (value: AdvancedLevel) => {
    if (value === AdvancedLevel.none) {
      setFilters({ difficulty: undefined });
    } else {
      setFilters({ difficulty: value });
    }
  };

  const renderSearchInput = () => {
    return (
      <div className="relative flex items-center max-w-lg w-full">
        <Search className="absolute left-3 size-4 text-muted-foreground pointer-events-none" />
        <Input
          className="pl-9"
          maxLength={64}
          onChange={(e) => setSearchText(e.target.value)}
          placeholder="Wyszukaj ćwiczenie"
          type="text"
          value={searchText}
        />
        {searchText && (
          <Button
            className="absolute right-0 ease-in-out duration-200"
            onClick={handleSearchClear}
            size="icon"
            variant="ghost"
          >
            <X className="size-4" />
          </Button>
        )}
      </div>
    );
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
                Grupy mięśniowe:
              </Typography>
              <MultiSelect
                defaultValue={
                  isMuscleGroupsLoading || isMuscleGroupError ? [] : (filters.muscleGroups ?? [])
                }
                disabled={isMuscleGroupsLoading || isMuscleGroupError}
                hideSelectAll={true}
                maxCount={3}
                modalPopover={true}
                onValueChange={(value) => setFilters({ muscleGroups: value })}
                options={apiResponseToMultiSelectOptions(muscleGroups)}
                placeholder={
                  isMuscleGroupsLoading
                    ? 'Ładowanie...'
                    : isMuscleGroupError
                      ? 'Błąd pobierania danych...'
                      : 'Wybierz grupy mięśniowe...'
                }
              />
            </div>

            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Sprzęt ćwiczeniowy:
              </Typography>
              <MultiSelect
                defaultValue={
                  isEquipmentLoading || isEquipmentError ? [] : (filters.equipment ?? [])
                }
                disabled={isEquipmentLoading || isEquipmentError}
                hideSelectAll={true}
                maxCount={3}
                modalPopover={true}
                onValueChange={(value) => setFilters({ equipment: value })}
                options={apiResponseToMultiSelectOptions(equipment)}
                placeholder={
                  isEquipmentLoading
                    ? 'Ładowanie...'
                    : isEquipmentError
                      ? 'Błąd pobierania danych...'
                      : 'Wybierz sprzęt...'
                }
              />
            </div>

            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Hasztagi:
              </Typography>
              <TagsInput
                id="exercise-filter-hashtags"
                onChange={(tags) => setFilters({ hashtags: tags })}
                placeholder="Podaj hasztagi, np. biceps, triceps - użyj Entera lub przecinka"
                value={filters.hashtags || []}
              />
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
                Pokaż ćwiczenia obserwowanych użytkowników:
              </Typography>
              <div className="flex flex-row items-center justify-between rounded-md border border-input p-3 bg-transparent dark:bg-input/30 shadow-xs">
                <div className="grid gap-1">
                  <Typography variant="muted">
                    Włącz, aby wyświetlać tylko ćwiczenia osób, które obserwujesz
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
          </div>
        </SheetContent>
      </Sheet>
    );
  };

  return (
    <>
      <div className="flex flex-col gap-4">
        <div className="flex flex-col md:flex-row gap-4 md:gap-2 md:items-end justify-center w-full">
          {renderSearchInput()}

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
              onClick={clearFilters}
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

        <HorizontalScrollFilter
          filters={filters}
          isLoading={isMuscleGroupsLoading || isMuscleGroupError}
          items={muscleGroups}
          label="Szukaj według grup mięśniowych"
          setFilters={setFilters}
          type="muscle-group"
        />

        <HorizontalScrollFilter
          filters={filters}
          isLoading={isEquipmentLoading || isEquipmentError}
          items={equipment}
          label="Szukaj według sprzętu treningowego"
          setFilters={setFilters}
          type="equipment"
        />
      </div>
      {renderFilterSheet()}
    </>
  );
};

export default ExerciseFilters;

type HorizontalScrollFilterProps = {
  filters: Filters;
  setFilters: (filters: Partial<Filters>) => void;
  items: Equipment[] | MuscleGroup[] | undefined;
  type: 'equipment' | 'muscle-group';
  label: string;
  isLoading: boolean;
};

const HorizontalScrollFilter = ({
  items,
  label,
  isLoading,
  type,
  filters,
  setFilters,
}: HorizontalScrollFilterProps) => {
  const isEquipment = type === 'equipment';

  const activeItems = useMemo(() => {
    return new Set(isEquipment ? filters.equipment : filters.muscleGroups);
  }, [isEquipment, filters.equipment, filters.muscleGroups]);

  const handleItemClick = useCallback(
    (id: string) => {
      if (isEquipment) {
        const currentEquipment = filters.equipment ?? [];
        const newEquipment = currentEquipment.includes(id)
          ? currentEquipment.filter((eq) => eq !== id)
          : [...currentEquipment, id];
        setFilters({ equipment: newEquipment });
      } else {
        const currentMuscleGroups = filters.muscleGroups ?? [];
        const newMuscleGroups = currentMuscleGroups.includes(id)
          ? currentMuscleGroups.filter((mg) => mg !== id)
          : [...currentMuscleGroups, id];
        setFilters({ muscleGroups: newMuscleGroups });
      }
    },
    [isEquipment, filters.equipment, filters.muscleGroups, setFilters],
  );

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent, id: string) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        handleItemClick(id);
      }
    },
    [handleItemClick],
  );

  const renderItem = useCallback(
    (item: Equipment | MuscleGroup) => {
      const isActive = activeItems.has(item._id);
      const itemType = isEquipment ? 'sprzęt treningowy' : 'grupa mięśniowa';

      return (
        <div
          aria-label={`${isActive ? 'Usuń' : 'Dodaj'} ${item.name} ${itemType}`}
          aria-pressed={isActive}
          className={cn(
            'group flex flex-col gap-3 items-center shrink-0 hover:bg-primary/10 border-2 border-transparent rounded-lg py-1 cursor-pointer transition-[background,border] ease-in-out duration-200 w-36',
            isActive && 'border-secondary bg-primary/10 hover:bg-primary/20',
          )}
          key={item._id}
          onClick={() => handleItemClick(item._id)}
          onKeyDown={(e) => handleKeyDown(e, item._id)}
          role="button"
          tabIndex={0}
        >
          <img
            alt={`${item.name} ${itemType}`}
            className={cn(
              'size-27 md:size-24 aspect-square rounded-md object-cover bg-primary/10 group-hover:bg-transparent transition-colors ease-in-out duration-200',
              isActive && 'bg-primary/20',
              isEquipment && 'rounded-full',
            )}
            loading="lazy"
            src={item.image}
          />

          <Typography
            aria-hidden="true"
            className="text-center text-base leading-tight"
            variant="small"
          >
            {item.name}
          </Typography>
        </div>
      );
    },
    [activeItems, isEquipment, handleItemClick, handleKeyDown],
  );

  return (
    <div>
      <Typography variant="h3">{label}</Typography>
      {isLoading ? (
        <Skeleton className="h-36 w-full mt-2 rounded-xl" />
      ) : (
        <div
          className="flex overflow-x-auto scrollbar gap-2 py-2 focus-within:outline-none"
          role="group"
        >
          {items!.map(renderItem)}
        </div>
      )}
    </div>
  );
};
