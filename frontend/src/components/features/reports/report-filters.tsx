import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Separator } from '@/components/ui/separator';
import Typography from '@/components/ui/typography';
import { ItemType } from '@/constants/enums/item-type';
import { ReportStatus } from '@/constants/enums/report-type';
import { cn } from '@/lib/utils';
import type { ReportFilters as Filters } from '@/types/filters';
import { Eraser } from 'lucide-react';
import { useCallback, useMemo } from 'react';

interface ReportFiltersProps {
  filters: Filters;
  setFilters: (filters: Partial<Filters>) => void;
  clearFilters: () => void;
}

function ReportFilters({ filters, setFilters, clearFilters }: ReportFiltersProps) {
  const selectedTypes = useMemo<ItemType[]>(
    () =>
      filters.reportedItemType === undefined
        ? []
        : Array.isArray(filters.reportedItemType)
          ? filters.reportedItemType
          : [filters.reportedItemType],
    [filters.reportedItemType],
  );

  const selectedStatuses = useMemo<ReportStatus[]>(
    () =>
      filters.status === undefined
        ? []
        : Array.isArray(filters.status)
          ? filters.status
          : [filters.status],
    [filters.status],
  );

  const toggleType = useCallback(
    (type: ItemType) => {
      const isSelected = selectedTypes.includes(type);
      const next = isSelected ? selectedTypes.filter((t) => t !== type) : [...selectedTypes, type];

      if (next.length === 0) {
        setFilters({ reportedItemType: undefined });
      } else {
        setFilters({ reportedItemType: next });
      }
    },
    [selectedTypes, setFilters],
  );

  const toggleStatus = useCallback(
    (status: ReportStatus) => {
      const isSelected = selectedStatuses.includes(status);
      const next = isSelected
        ? selectedStatuses.filter((s) => s !== status)
        : [...selectedStatuses, status];

      if (next.length === 0) {
        setFilters({ status: undefined });
      } else {
        setFilters({ status: next });
      }
    },
    [selectedStatuses, setFilters],
  );

  return (
    <div className="flex flex-col gap-2 p-4 bg-card rounded-md border shadow-sm">
      <div className="flex items-center justify-between mb-2 min-h-8">
        <Typography variant="h3">Filtruj zgłoszenia</Typography>
        {(selectedStatuses.length !== 0 || selectedTypes.length !== 0) && (
          <Button onClick={clearFilters} size="sm" variant="ghost">
            <Eraser />
            Wyczyść filtry
          </Button>
        )}
      </div>
      <div className="grid grid-cols-1 md:grid-cols-[1fr_0.025fr_1fr] gap-6">
        <FilterGroup<ItemType>
          onToggle={toggleType}
          options={Object.values(ItemType)}
          selected={selectedTypes}
          title="Typ zgłoszonej treści"
        />
        <Separator className="mx-auto hidden md:block" orientation="vertical" />
        <Separator className="md:hidden" orientation="horizontal" />
        <FilterGroup<ReportStatus>
          onToggle={toggleStatus}
          options={Object.values(ReportStatus)}
          selected={selectedStatuses}
          title="Status zgłoszenia"
        />
      </div>
    </div>
  );
}

interface FilterGroupProps<T extends string> {
  title: string;
  options: T[];
  selected: T[];
  onToggle: (option: T) => void;
}

function FilterGroup<T extends string>({
  title,
  options,
  selected,
  onToggle,
}: FilterGroupProps<T>) {
  return (
    <div className="flex-1">
      <Typography className="text-base mb-1 text-muted-foreground">{title}</Typography>
      <fieldset className="grid grid-cols-1 sm:grid-cols-2 gap-2">
        <legend className="sr-only">{title}</legend>
        {options.map((option) => {
          const id = `${title}-${option}`;
          const checked = selected.includes(option);

          return (
            <label
              className={cn(
                'flex items-center gap-2 cursor-pointer select-none p-2 bg-accent/50 text-card-foreground rounded-md border transition-all hover:border-secondary/50',
                checked && 'border-secondary',
              )}
              htmlFor={id}
              key={option}
            >
              <Checkbox
                checked={checked}
                className="dark:data-[state=checked]:bg-secondary data-[state=checked]:bg-primary data-[state=checked]:border-secondary data-[state=checked]:text-secondary-foreground"
                id={id}
                onCheckedChange={() => onToggle(option)}
              />
              <Typography className="text-sm" variant="small">
                {option}
              </Typography>
            </label>
          );
        })}
      </fieldset>
    </div>
  );
}

export default ReportFilters;
