import { CircleFlag } from 'react-circle-flags';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { cn } from '@/lib/utils';
import { ChevronsUpDown, Check } from 'lucide-react';
import { useMemo, useState } from 'react';
import { Button } from './button';

interface CountryDropdownProps {
  options: { label: string; value: string }[];
  value: string;
  onChange: (country: string) => void;
  id?: string;
}

function CountryDropdown({ options, value, onChange, id }: CountryDropdownProps) {
  const [open, setOpen] = useState(false);

  const selectedOption = useMemo(() => {
    return options.find((option) => option.label === value);
  }, [value, options]);

  return (
    <Popover onOpenChange={setOpen} open={open}>
      <PopoverTrigger asChild>
        <Button
          aria-expanded={open}
          className="justify-between "
          id={id}
          role="combobox"
          type="button"
          variant="outline"
        >
          <div className="flex gap-2 items-center">
            {selectedOption && selectedOption.value !== 'NONE' && (
              <div className="inline-flex items-center justify-center w-5 h-5 shrink-0 overflow-hidden rounded-full">
                <CircleFlag countryCode={selectedOption.value.toLowerCase()} height={20} />
              </div>
            )}
            <span>{selectedOption ? selectedOption.label : 'Wybierz kraj'}</span>
          </div>
          <ChevronsUpDown className="opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="p-0 min-w-[--radix-popover-trigger-width]" side="bottom">
        <Command className=" max-h-[200px] sm:max-h-[270px]">
          <CommandList className="scrollbar">
            <div className="sticky top-0 z-10 bg-popover">
              <CommandInput placeholder="Wyszukaj kraj..." />
            </div>
            <CommandEmpty>Nie znaleziono kraju.</CommandEmpty>
            <CommandGroup>
              {options.map((option) => (
                <CommandItem
                  className="flex items-center w-full gap-2 cursor-pointer"
                  key={option.value}
                  onSelect={() => {
                    onChange(option.label);
                    setOpen(false);
                  }}
                  value={option.label}
                >
                  <div className="flex gap-2 items-center">
                    {option.value !== 'NONE' && (
                      <div className="inline-flex items-center justify-center w-5 h-5 shrink-0 overflow-hidden rounded-full">
                        <CircleFlag countryCode={option.value.toLowerCase()} height={20} />
                      </div>
                    )}
                    <span>{option.label}</span>
                  </div>
                  <Check
                    className={cn('ml-auto', value === option.label ? 'opacity-100' : 'opacity-0')}
                  />
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}

export default CountryDropdown;
