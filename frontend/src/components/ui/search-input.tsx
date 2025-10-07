import { Search, X } from 'lucide-react';
import { Input } from './input';
import { Button } from './button';
import { useId } from 'react';
import { cn } from '@/lib/utils';

interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  onClear: () => void;
  maxLength?: number;
  placeholder?: string;
  className?: string;
}

function SearchInput({
  value,
  onChange,
  onClear,
  maxLength,
  placeholder,
  className,
}: SearchInputProps) {
  const id = useId();

  return (
    <div className={cn('relative flex items-center w-full', className)}>
      <Search className="absolute left-3 size-4 text-muted-foreground pointer-events-none" />
      <Input
        className="pl-9"
        id={id}
        maxLength={maxLength}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        type="text"
        value={value}
      />
      {value && (
        <Button
          className="absolute right-0 ease-in-out duration-200"
          onClick={onClear}
          size="icon"
          variant="ghost"
        >
          <X className="size-4" />
        </Button>
      )}
    </div>
  );
}

export default SearchInput;
