import { Minus, Plus } from 'lucide-react';
import { Button } from './button';
import { Input } from './input';
import { Label } from './label';
import { useEffect, useId, useState } from 'react';
import { cn } from '@/lib/utils';
import Typography from './typography';

interface NumberInputProps {
  value: number;
  onChange: (value: number) => void;
  id?: string;
  label?: string;
  placeholder?: string;
  min?: number;
  max?: number;
  step?: number;
  suffix?: string;
  className?: string;
  labelClassName?: string;
  disabled?: boolean;
  autoFocus?: boolean;
}

function NumberInput({
  value,
  label,
  onChange,
  placeholder,
  id,
  min = 0,
  max = Infinity,
  step = 1,
  suffix,
  labelClassName,
  autoFocus = false,
  disabled = false,
  className,
}: NumberInputProps) {
  const uid = useId();
  const [internalValue, setInternalValue] = useState<string>(value === 0 ? '' : String(value));

  useEffect(() => {
    if (document.activeElement?.id !== (id || uid)) {
      setInternalValue(value === 0 ? '' : String(value));
    }
  }, [value, id, uid]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInternalValue(e.target.value);
  };

  const handleBlur = () => {
    let val = Number(internalValue);
    if (isNaN(val)) {
      setInternalValue(value === 0 ? '' : String(value));
      return;
    }
    if (val < min) val = min;
    if (val > max) val = max;
    setInternalValue(val === 0 ? '' : String(val));
    onChange(val);
  };

  const increment = () => {
    if (value !== null) {
      const newVal = value + step;
      onChange(newVal > max ? max : newVal);
    }
  };

  const decrement = () => {
    if (value !== null) {
      const newVal = value - step;
      onChange(newVal < min ? min : newVal);
    }
  };

  const component = (
    <div className="inline-flex gap-2">
      <Button
        disabled={value <= min || disabled}
        onClick={decrement}
        size="icon"
        type="button"
        variant="outline"
      >
        <Minus />
      </Button>
      <div className="relative flex items-center">
        <Input
          autoFocus={autoFocus}
          className={cn('w-20 text-center', className)}
          disabled={disabled}
          id={id || uid}
          inputMode="numeric"
          max={max}
          min={min}
          onBlur={handleBlur}
          onChange={handleChange}
          placeholder={placeholder}
          step={step}
          type="number"
          value={internalValue}
        />
        {suffix && value !== 0 && (
          <Typography className="absolute right-2 pointer-events-none" variant="muted">
            {suffix}
          </Typography>
        )}
      </div>
      <Button
        disabled={value >= max || disabled}
        onClick={increment}
        size="icon"
        type="button"
        variant="outline"
      >
        <Plus />
      </Button>
    </div>
  );

  if (label) {
    return (
      <div className="flex flex-col gap-2">
        <Label className={cn('justify-center', labelClassName)} htmlFor={uid}>
          {label}
        </Label>
        {component}
      </div>
    );
  }

  return component;
}

export default NumberInput;
