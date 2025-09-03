import type { AdvancedLevel } from '@/constants/advanced-level';
import { Badge } from './badge';
import type { Visibility } from '@/constants/visibility';
import { Globe2, Lock } from 'lucide-react';
import { cn } from '@/lib/utils';

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
};

const VisibilityBadge = ({ visibility, type }: VisibilityBadgeProps) => {
  const isPublic = visibility === 'Publiczny';
  const icon = isPublic ? <Globe2 className="size-5" /> : <Lock className="size-5" />;
  const label = `${isPublic ? 'Publiczn' : 'Prywatn'}${type === 'Workout' ? 'y' : 'e'}`;
  return (
    <Badge className="min-h-7">
      <div>{icon}</div>
      {label}
    </Badge>
  );
};

export { AdvancedLevelBadge, VisibilityBadge };
