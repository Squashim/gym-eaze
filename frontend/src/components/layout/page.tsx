import type { PropsWithChildren } from 'react';
import Typography from '../ui/typography';
import { cn } from '@/lib/utils';
import Footer from './footer';

type PageLayoutProps = {
  title?: string;
  description?: string;
  className?: string;
  withFooter?: boolean;
  titleClassName?: string;
};

const PageLayout = ({
  className,
  titleClassName,
  title,
  description,
  withFooter = true,
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

      {withFooter && <Footer />}
    </div>
  );
};

export default PageLayout;
