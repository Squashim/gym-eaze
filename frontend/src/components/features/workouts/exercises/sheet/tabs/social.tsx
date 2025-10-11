import Typography from '@/components/ui/typography';
import ExerciseList from '../exercise-list';
import { Switch } from '@/components/ui/switch';
import type { WorkoutSheetTabProps } from '@/types/workout';
import useFilterQuery from '@/hooks/filters-pagination/use-filter-query';
import type { ExerciseFilters, SortBy } from '@/types/filters';
import { defaultExerciseFilters } from '@/constants/defaults/filters';
import useGetExercises from '@/hooks/query/exercises/use-get-exercises';
import { useEffect, useMemo, useState } from 'react';
import useDebounce from '@/hooks/utils/use-debounce';
import useGetMuscleGroups from '@/hooks/query/muscle-groups/use-get-muscle-groups';
import useGetEquipment from '@/hooks/query/equipment/use-get-equipment';
import { AdvancedLevel } from '@/constants/enums/advanced-level';
import { ArrowUpDown, Eraser, Filter } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { Badge } from '@/components/ui/badge';
import { apiResponseToMultiSelectOptions, cn } from '@/lib/utils';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { MultiSelect } from '@/components/ui/multi-select';
import { TagsInput } from '@/components/ui/tags-input';
import SearchInput from '@/components/ui/search-input';

const SocialExercisesTab = ({ onExerciseToggle, isExerciseAdded }: WorkoutSheetTabProps) => {
  const { params, setPage, setFilters, setSortBy, clearFilters } =
    useFilterQuery<ExerciseFilters>(defaultExerciseFilters);
  const { data: exercises, isLoading, isError } = useGetExercises(params);
  const [searchText, setSearchText] = useState(params.filters?.search ?? '');
  const [isMoreFiltersOpen, setIsMoreFiltersOpen] = useState(false);
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

  const activeFiltersCount = useMemo(() => {
    return Object.entries(params.filters ?? {}).reduce((acc, [key, value]) => {
      if (key === 'search') return acc;
      if (Array.isArray(value) && value.length > 0) return acc + 1;
      if (typeof value === 'boolean' && value) return acc + 1;
      if (typeof value === 'string' && value !== '') return acc + 1;
      return acc;
    }, 0);
  }, [params.filters]);

  const muscleGroupOptions = useMemo(
    () => apiResponseToMultiSelectOptions(muscleGroups),
    [muscleGroups],
  );
  const equipmentOptions = useMemo(() => apiResponseToMultiSelectOptions(equipment), [equipment]);

  useEffect(() => {
    setFilters({ search: debouncedSearch });
  }, [debouncedSearch, setFilters]);

  const handleSearchClear = () => {
    setSearchText('');
    setFilters({ search: '' });
  };

  const handleMoreFiltersToggle = () => {
    setIsMoreFiltersOpen(!isMoreFiltersOpen);
  };

  const handleAdvancedLevelChange = (value: AdvancedLevel) => {
    if (value === AdvancedLevel.none) {
      setFilters({ difficulty: undefined });
    } else {
      setFilters({ difficulty: value });
    }
  };

  return (
    <>
      <header className="flex items-center w-full gap-2 sm:gap-3">
        <SearchInput
          maxLength={64}
          onChange={(newValue) => setSearchText(newValue)}
          onClear={handleSearchClear}
          placeholder="Wyszukaj ćwiczenie..."
          value={searchText}
        />

        <div className="flex gap-1 sm:gap-2 shrink-0">
          <Tooltip>
            <TooltipTrigger asChild>
              <Button className="relative" onClick={handleMoreFiltersToggle} size="icon">
                <Filter />
                {activeFiltersCount > 0 && (
                  <Badge
                    className="absolute -top-1 -right-1 rounded-full min-w-5 h-5 px-1 tabular-nums"
                    variant="secondary"
                  >
                    {activeFiltersCount}
                  </Badge>
                )}
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <Typography variant="small">
                {isMoreFiltersOpen ? 'Ukryj filtry' : 'Pokaż filtry'}
              </Typography>
            </TooltipContent>
          </Tooltip>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                className={cn(activeFiltersCount > 0 ? 'visible' : 'invisible')}
                onClick={clearFilters}
                size="icon"
                variant="ghost"
              >
                <Eraser />
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <Typography variant="small">Wyczyść filtry</Typography>
            </TooltipContent>
          </Tooltip>
        </div>
      </header>
      <div
        className={cn(
          'overflow-hidden transition-all duration-300 ease-in-out flex flex-col gap-3 mt-3 p-0.5',
          isMoreFiltersOpen ? 'max-h-[600px] opacity-100 mb-3' : 'max-h-0 opacity-0',
        )}
      >
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="grid gap-2">
            <Typography className="font-semibold" variant="small">
              Sortuj według:
            </Typography>
            <Select onValueChange={(value) => setSortBy(value as SortBy)} value={params.sortBy}>
              <SelectTrigger className="w-full" tabIndex={isMoreFiltersOpen ? 0 : -1}>
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

          <div className="grid gap-2">
            <Typography className="font-semibold" variant="small">
              Poziom trudności:
            </Typography>
            <Select
              onValueChange={handleAdvancedLevelChange}
              value={params.filters?.difficulty ?? AdvancedLevel.none}
            >
              <SelectTrigger className="w-full" tabIndex={isMoreFiltersOpen ? 0 : -1}>
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
        </div>

        <div className="grid gap-2">
          <Typography className="font-semibold" variant="small">
            Grupy mięśniowe:
          </Typography>
          <MultiSelect
            defaultValue={
              isMuscleGroupsLoading || isMuscleGroupError
                ? []
                : (params.filters?.muscleGroups ?? [])
            }
            disabled={isMuscleGroupsLoading || isMuscleGroupError}
            hideSelectAll={true}
            maxCount={3}
            modalPopover={true}
            onValueChange={(value) => setFilters({ muscleGroups: value })}
            options={muscleGroupOptions}
            placeholder={
              isMuscleGroupsLoading
                ? 'Ładowanie...'
                : isMuscleGroupError
                  ? 'Błąd pobierania danych...'
                  : 'Wybierz grupy mięśniowe...'
            }
            tabIndex={isMoreFiltersOpen ? 0 : -1}
          />
        </div>

        <div className="grid gap-2">
          <Typography className="font-semibold" variant="small">
            Sprzęt ćwiczeniowy:
          </Typography>
          <MultiSelect
            defaultValue={
              isEquipmentLoading || isEquipmentError ? [] : (params.filters?.equipment ?? [])
            }
            disabled={isEquipmentLoading || isEquipmentError}
            hideSelectAll={true}
            maxCount={3}
            modalPopover={true}
            onValueChange={(value) => setFilters({ equipment: value })}
            options={equipmentOptions}
            placeholder={
              isEquipmentLoading
                ? 'Ładowanie...'
                : isEquipmentError
                  ? 'Błąd pobierania danych...'
                  : 'Wybierz sprzęt...'
            }
            tabIndex={isMoreFiltersOpen ? 0 : -1}
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
            tabIndex={isMoreFiltersOpen ? 0 : -1}
            value={params.filters?.hashtags || []}
          />
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
              checked={params.filters?.followers === true}
              onCheckedChange={(checked) => setFilters({ followers: checked ? true : undefined })}
              tabIndex={isMoreFiltersOpen ? 0 : -1}
            />
          </div>
        </div>
      </div>
      <div className="flex flex-col h-full pb-3 max-h-[63dvh] sm:max-h-[calc(100dvh_-_14.5rem)] min-h-[30dvh]">
        <ExerciseList
          exercises={exercises}
          isError={isError}
          isExerciseAdded={isExerciseAdded}
          isLoading={isLoading}
          listType="social"
          onExerciseToggle={onExerciseToggle}
          params={params}
          setPage={setPage}
        />
      </div>
    </>
  );
};

export default SocialExercisesTab;
