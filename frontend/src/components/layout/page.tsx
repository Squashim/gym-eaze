import type { PropsWithChildren } from 'react';
import Typography from '../ui/typography';
import { cn } from '@/lib/utils';

type PageLayoutProps = {
  title?: string;
  description?: string;
  className?: string;
  titleClassName?: string;
};

const PageLayout = ({
  className,
  titleClassName,
  title,
  description,
  children,
}: PropsWithChildren<PageLayoutProps>) => {
  return (
    <div className={cn('flex flex-col gap-6 my-6 w-full h-full', className)}>
      <header className="grid gap-1 ">
        <Typography className={cn('text-left', titleClassName)} variant="h1">
          {title}
        </Typography>
        {description && (
          <Typography className="text-left text-balance" variant="lead">
            {description}
          </Typography>
        )}
      </header>
      {children}
    </div>
  );
};

export default PageLayout;
