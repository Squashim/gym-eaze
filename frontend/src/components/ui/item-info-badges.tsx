import type { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Badge } from './badge';
import type { Visibility } from '@/constants/enums/visibility';
import { Globe2, Heart, Loader2, Lock, ThumbsUp } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';

function AdvancedLevelBadge({ advancedLevel }: { advancedLevel: AdvancedLevel }) {
  const badgeStyles = {
    'Nie wybrano': 'bg-accent text-accent-foreground shadow-sm',
    Początkujący: 'bg-secondary text-secondary-foreground',
    Średniozaawansowany:
      'text-yellow-800 dark:text-yellow-500 bg-yellow-500/10 dark:bg-yellow-600/10',
    Zaawansowany: 'text-destructive bg-destructive/10',
  };

  return <Badge className={cn('min-h-7', badgeStyles[advancedLevel])}>{advancedLevel}</Badge>;
}

type VisibilityBadgeProps = {
  visibility: Visibility;
  type: 'Exercise' | 'Workout';
  variant?: 'default' | 'outline' | 'accent';
};

const VisibilityBadge = ({ visibility, type, variant = 'default' }: VisibilityBadgeProps) => {
  const isPublic = visibility === 'Publiczny';
  const icon = isPublic ? <Globe2 className="size-4 mr-0.5" /> : <Lock className="size-4 mr-0.5" />;
  const label = `${isPublic ? 'Publiczn' : 'Prywatn'}${type === 'Workout' ? 'y' : 'e'}`;
  return (
    <Badge className="min-h-7" variant={variant}>
      <div>{icon}</div>
      {label}
    </Badge>
  );
};

type LikesBadgeProps = {
  likes: number;
};

const LikesBadge = ({ likes }: LikesBadgeProps) => {
  return (
    <Badge className="min-h-7 text-sm px-3" variant="accent">
      <div>
        <ThumbsUp className="size-4 mr-0.5" />
      </div>
      {likes}
    </Badge>
  );
};

type ItemDetailsBadgeProps = {
  isLoading?: boolean;
  isLiked?: boolean;
  isFavorited?: boolean;
  isPublic: boolean;
  isUserPage: boolean;
};

const ItemDetailsBadge = ({
  isLoading,
  isLiked = false,
  isFavorited = false,
  isPublic,
  isUserPage,
}: ItemDetailsBadgeProps) => {
  return (
    <div className="inline-flex absolute top-2 right-2 gap-2 items-center">
      {isLoading && (
        <Badge className="h-7 w-16" variant="accent">
          <div>
            <Loader2 className="animate-spin size-4" />
          </div>
        </Badge>
      )}
      {!isLoading && isLiked && (
        <Badge className="size-7 rounded-full px-1 font-mono tabular-nums" variant="secondary">
          <div>
            <ThumbsUp className="size-4" />
          </div>
        </Badge>
      )}

      {!isLoading && isFavorited && (
        <Badge className="size-7 rounded-full px-1 font-mono tabular-nums" variant="accent">
          <div>
            <Heart className="size-4 fill-destructive text-destructive" />
          </div>
        </Badge>
      )}

      {isUserPage && (
        <Badge className="size-7 rounded-full px-1 font-mono tabular-nums">
          <div>{isPublic ? <Globe2 className="size-4" /> : <Lock className="size-4" />}</div>
        </Badge>
      )}
    </div>
  );
};

interface IsPublicBadgeProps {
  isPublic?: boolean;
  isLoading: boolean;
}

const IsPublicBadge = ({ isPublic, isLoading }: IsPublicBadgeProps) => {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Badge className="size-7 rounded-full px-1 font-mono tabular-nums" tabIndex={0}>
          <div>
            {isLoading ? (
              <Loader2 className="animate-spin size-4" />
            ) : isPublic ? (
              <Globe2 className="size-4" />
            ) : (
              <Lock className="size-4" />
            )}
          </div>
        </Badge>
      </TooltipTrigger>
      <TooltipContent>
        {isPublic
          ? 'Te informacje są widoczne dla innych użytkowników'
          : 'Te informacje są widoczne tylko dla Ciebie'}
      </TooltipContent>
    </Tooltip>
  );
};

export { AdvancedLevelBadge, VisibilityBadge, LikesBadge, ItemDetailsBadge, IsPublicBadge };
