import useScrollToTop from '@/hooks/utils/use-scroll-to-top';
import { Button } from './button';
import { ChevronUp } from 'lucide-react';
import { cn } from '@/lib/utils';

interface ScrollToTopButtonProps {
  vh?: number;
}

function ScrollToTopButton({ vh }: ScrollToTopButtonProps) {
  const { show, scrollToTop } = useScrollToTop(vh);

  return (
    <Button
      aria-label="Wróć na górę"
      className={cn(
        'fixed bottom-6 right-6 z-40 size-10 shadow-lg transition-[transform,opacity] duration-100 ease-in-out',
        show
          ? 'opacity-100 translate-y-0 pointer-events-auto'
          : 'opacity-0 translate-y-4 pointer-events-none',
      )}
      onClick={scrollToTop}
      size="icon"
      variant="secondary"
    >
      <ChevronUp className="size-6" />
    </Button>
  );
}

export default ScrollToTopButton;
