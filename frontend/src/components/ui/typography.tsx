import { cn } from '@/lib/utils';
import type { ReactNode } from 'react';

interface Props {
  className?: string;
  children: ReactNode;
}

const H1 = ({ className, children }: Props) => {
  return (
    <h1
      className={cn(
        'scroll-m-20 text-center text-4xl font-extrabold tracking-tight text-balance',
        className,
      )}
    >
      {children}
    </h1>
  );
};

const H2 = ({ className, children }: Props) => {
  return (
    <h2
      className={cn(
        'scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight first:mt-0',
        className,
      )}
    >
      {children}
    </h2>
  );
};

const H3 = ({ className, children }: Props) => {
  return (
    <h3 className={cn('scroll-m-20 text-2xl font-semibold tracking-tight', className)}>
      {children}
    </h3>
  );
};

const H4 = ({ className, children }: Props) => {
  return (
    <h4 className={cn('scroll-m-20 text-xl font-semibold tracking-tight', className)}>
      {children}
    </h4>
  );
};

const P = ({ className, children }: Props) => {
  return <p className={cn('leading-7 [&:not(:first-child)]:mt-6', className)}>{children}</p>;
};

const Lead = ({ className, children }: Props) => {
  return <p className={cn('text-muted-foreground text-xl', className)}>{children}</p>;
};

const Small = ({ className, children }: Props) => {
  return <small className={cn('text-sm leading-none font-medium', className)}>{children}</small>;
};

const Muted = ({ className, children }: Props) => {
  return <p className={cn('text-muted-foreground text-sm', className)}>{children}.</p>;
};

export { H1, H2, H3, H4, P, Lead, Small, Muted };
