import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import type { ProfileBadge } from '@/types/badge';

interface PinnedBadgesProps {
  badges: ProfileBadge[];
}

function PinnedBadges({ badges }: PinnedBadgesProps) {
  return (
    <div className="grid grid-cols-4 sm:grid-cols-5 gap-2 items-center place-items-center">
      {badges.map((badge) => (
        <Tooltip key={badge._id}>
          <TooltipTrigger asChild>
            <img
              alt={`odznaka ${badge.title}`}
              className="aspect-square rounded-md object-cover w-full h-auto max-w-18"
              loading="lazy"
              src={badge.image}
              tabIndex={0}
            />
          </TooltipTrigger>
          <TooltipContent className="grid gap-1 max-w-sm">
            <Typography className="font-semibold text-sm leading-normal" variant="p">
              {badge.title}
            </Typography>
            <Typography className="leading-normal text-primary-foreground/80" variant="small">
              {badge.description}
            </Typography>
            <Typography className="text-xs leading-normal mt-1" variant="p">
              Zdobyto: {new Date(badge.awardedAt).toLocaleString()}
            </Typography>
          </TooltipContent>
        </Tooltip>
      ))}
    </div>
  );
}

export default PinnedBadges;
