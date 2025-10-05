import { cn } from '@/lib/utils';
import type { PropsWithChildren } from 'react';
import { type LucideIcon } from 'lucide-react';
import Typography, { type Variant } from './typography';

const InfoField = ({
  label,
  fullWidth = false,
  children,
  variant = 'small',
  className,
}: PropsWithChildren & {
  fullWidth?: boolean;
  className?: string;
  variant?: Variant;
  label: string;
}) => (
  <div className={cn('flex flex-col gap-1', fullWidth && 'md:col-span-full', className)}>
    <Typography className="font-semibold" variant={variant}>
      {label}:
    </Typography>
    {children}
  </div>
);

const IconInfoField = ({ label, icon: Icon }: { icon: LucideIcon; label: string }) => (
  <div className="flex gap-2 items-center px-3 rounded-md bg-accent-foreground/10 w-fit min-h-12">
    <Icon className="size-6 text-secondary" />
    <Typography className="font-semibold text-lg">{label}</Typography>
  </div>
);

export { InfoField, IconInfoField };
