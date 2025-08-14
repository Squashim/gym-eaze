import * as React from 'react';

import { cn } from '@/lib/utils';

function Input({
  className,
  type,
  value,
  onBlur,
  onChange,
  ...props
}: React.ComponentProps<'input'>) {
  const handleBlur: React.FocusEventHandler<HTMLInputElement> = (e) => {
    if (typeof e.target.value === 'string' && e.target.value !== e.target.value.trim()) {
      const trimmedValue = e.target.value.trim();

      if (onChange) {
        const syntheticEvent = {
          ...e,
          target: { ...e.target, value: trimmedValue },
        } as React.ChangeEvent<HTMLInputElement>;
        onChange(syntheticEvent);
      }
    }

    if (onBlur) {
      onBlur(e);
    }
  };

  return (
    <input
      className={cn(
        'file:text-foreground placeholder:text-muted-foreground selection:bg-primary selection:text-primary-foreground dark:bg-input/30 border-input flex h-9 w-full min-w-0 rounded-md border bg-transparent px-3 py-1 text-base shadow-xs transition-[color,box-shadow] outline-none file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm',
        'focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]',
        'aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive',
        className,
      )}
      data-slot="input"
      onBlur={handleBlur}
      onChange={onChange}
      type={type}
      value={value}
      {...props}
    />
  );
}

export { Input };
