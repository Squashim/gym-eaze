import type { AdvancedLevel } from '@/constants/enums/advanced-level';
import { Badge } from './badge';
import type { Visibility } from '@/constants/enums/visibility';
import {
  BicepsFlexed,
  CalendarDaysIcon,
  Circle,
  CircleCheck,
  CircleX,
  FileText,
  Globe2,
  Heart,
  Loader2,
  Lock,
  MessageSquare,
  ThumbsUp,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';
import { useMemo } from 'react';
import type { ReportStatus } from '@/constants/enums/report-type';
import type { ItemType } from '@/constants/enums/item-type';

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

const BmiBadge = ({ bmi }: { bmi: number }) => {
  const classification = useMemo(() => {
    if (bmi < 18.5) return 'Niedowaga';
    if (bmi >= 18.5 && bmi < 24.9) return 'Waga prawidłowa';
    if (bmi >= 25 && bmi < 29.9) return 'Nadwaga';
    return 'Otyłość';
  }, [bmi]);

  const badgeStyles = {
    Niedowaga: 'dark:bg-blue-800 bg-blue-300 text-accent-foreground shadow-sm',
    'Waga prawidłowa': 'bg-secondary text-secondary-foreground',
    Nadwaga: 'text-yellow-800 dark:text-yellow-500 bg-yellow-500/10 dark:bg-yellow-600/10',
    Otyłość: 'text-destructive bg-destructive/10',
  };

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Badge className={cn('cursor-default', badgeStyles[classification])}>BMI: {bmi}</Badge>
      </TooltipTrigger>
      <TooltipContent>
        <Typography variant="small">
          {`BMI wynosi ${bmi}, co zalicza się do: ${classification}.`}
        </Typography>
      </TooltipContent>
    </Tooltip>
  );
};

const ReportStatusBadge = ({ status }: { status: ReportStatus }) => {
  const badgeStyles = {
    Oczekujący: 'bg-accent text-accent-foreground shadow-sm',
    Odrzucony: 'text-destructive bg-destructive/10',
    Rozwiązany: 'bg-secondary text-secondary-foreground',
  };

  return (
    <Badge className={cn('min-h-7', badgeStyles[status])}>
      {status === 'Oczekujący' ? (
        <Circle />
      ) : status === 'Rozwiązany' ? (
        <CircleCheck />
      ) : (
        <CircleX />
      )}
      {status}
    </Badge>
  );
};

const ItemTypeBadge = ({ type }: { type: ItemType }) => {
  const badgeIcons = {
    Post: FileText,
    Komentarz: MessageSquare,
    Trening: CalendarDaysIcon,
    Ćwiczenie: BicepsFlexed,
  };

  const Icon = badgeIcons[type];

  return (
    <Badge className="min-h-7">
      <div>
        <Icon className="size-4" />
      </div>
      {type}
    </Badge>
  );
};

export {
  AdvancedLevelBadge,
  VisibilityBadge,
  LikesBadge,
  ItemDetailsBadge,
  IsPublicBadge,
  BmiBadge,
  ItemTypeBadge,
  ReportStatusBadge,
};
