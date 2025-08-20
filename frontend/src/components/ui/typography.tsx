import { cn } from '@/lib/utils';
import type { ElementType, ReactNode } from 'react';

type Variant = keyof typeof typographyVariants;

type TypographyProps = {
  as?: ElementType;
  variant: Variant;
  className?: string;
  children: ReactNode;
};

const typographyVariants = {
  h1: 'scroll-m-20 text-center text-4xl font-extrabold tracking-tight text-balance',
  h2: 'scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight first:mt-0',
  h3: 'scroll-m-20 text-2xl font-semibold tracking-tight',
  h4: 'scroll-m-20 text-xl font-semibold tracking-tight',
  p: 'leading-7',
  lead: 'text-muted-foreground text-xl',
  small: 'text-sm leading-none font-medium',
  muted: 'text-muted-foreground text-sm',
} as const;

const typographyElementMap: Record<Variant, ElementType> = {
  h1: 'h1',
  h2: 'h2',
  h3: 'h3',
  h4: 'h4',
  p: 'p',
  lead: 'p',
  small: 'small',
  muted: 'span',
};

function Typography({ variant = 'p', as, className, children }: TypographyProps) {
  const Component: ElementType = as || typographyElementMap[variant];

  return <Component className={cn(typographyVariants[variant], className)}>{children}</Component>;
}

export default Typography;
