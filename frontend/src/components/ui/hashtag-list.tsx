import { cn } from '@/lib/utils';
import { Badge } from './badge';
import { InfoField } from './info-fields';
import type { Variant } from './typography';

interface HashtagListProps {
  hashtags: string[];
  fullWidth?: boolean;
  className?: string;
  variant?: Variant;
  badgeClassName?: string;
}

function HashtagList({
  hashtags,
  fullWidth = true,
  className,
  badgeClassName,
  variant = 'small',
}: HashtagListProps) {
  if (!hashtags.length) return null;

  return (
    <InfoField fullWidth={fullWidth} label="Hasztagi" variant={variant}>
      <div className={cn('flex flex-wrap gap-1 max-h-16 overflow-y-auto scrollbar', className)}>
        {hashtags.map((hashtag) => (
          <Badge className={badgeClassName} key={hashtag} variant="outline">
            {`#${hashtag}`}
          </Badge>
        ))}
      </div>
    </InfoField>
  );
}

export default HashtagList;
