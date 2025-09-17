import { Minus, Plus } from 'lucide-react';
import { Button } from './button';
import { Input } from './input';
import { Label } from './label';
import { useId } from 'react';
import { cn } from '@/lib/utils';
import Typography from './typography';

interface NumberInputProps {
  value: number;
  label: string;
  onChange: (value: number) => void;
  placeholder?: string;
  min?: number;
  max?: number;
  step?: number;
  suffix?: string;
  className?: string;
}

function NumberInput({
  value,
  label,
  onChange,
  placeholder,
  min = Number.MIN_SAFE_INTEGER,
  max = Number.MAX_SAFE_INTEGER,
  step = 1,
  suffix,
  className,
}: NumberInputProps) {
  const uid = useId();
  const increment = () => onChange(Math.min((value || 0) + step, max));
  const decrement = () => onChange(Math.max((value || 0) - step, min));

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const parsed = parseFloat(e.target.value);
    if (!isNaN(parsed)) {
      if (parsed > max) return onChange(max);
      if (parsed < min) return onChange(min);
      onChange(parsed);
    } else {
      onChange(min);
    }
  };

  return (
    <div className="flex flex-col gap-2">
      <Label className="justify-center" htmlFor={uid}>
        {label}
      </Label>
      <div className="inline-flex gap-2">
        <Button onClick={decrement} size="icon" type="button" variant="outline">
          <Minus />
        </Button>
        <div className="relative flex items-center">
          <Input
            className={cn('w-20 text-center', className)}
            id={uid}
            max={max}
            min={min}
            onChange={handleChange}
            placeholder={placeholder}
            step={step}
            type="number"
            value={value}
          />
          {suffix && (
            <Typography className="absolute right-2 pointer-events-none" variant="muted">
              {suffix}
            </Typography>
          )}
        </div>
        <Button onClick={increment} size="icon" type="button" variant="outline">
          <Plus />
        </Button>
      </div>
    </div>
  );
}

export default NumberInput;
