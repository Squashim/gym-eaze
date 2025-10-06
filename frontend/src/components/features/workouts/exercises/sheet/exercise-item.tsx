import { Button } from '@/components/ui/button';
import { AdvancedLevelBadge, LikesBadge } from '@/components/ui/item-info-badges';
import { DetailsActionBtn } from '@/components/ui/reusable-buttons';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { cn } from '@/lib/utils';
import type { GetExercisesApiResponse } from '@/types/exercise';
import { Plus, Trash2 } from 'lucide-react';
import { useCallback } from 'react';

interface ExerciseItemProps {
  item: GetExercisesApiResponse;
  onExerciseToggle: (id: string) => void;
  isExerciseAdded: (id: string) => boolean;
}

const ExerciseItem = ({ item, onExerciseToggle, isExerciseAdded }: ExerciseItemProps) => {
  const displayMuscleGroups = item.muscleGroup.map((mg) => mg.name).join(' • ');
  const isAdded = isExerciseAdded(item._id);

  const handleExerciseToggle = useCallback(
    (id: string) => onExerciseToggle(id),
    [onExerciseToggle],
  );

  return (
    <div className="rounded-md p-3 shadow-md flex flex-col sm:flex-row gap-3 border bg-card text-card-foreground min-h-26 max-sm:min-h-auto">
      <div className="flex gap-3 sm:flex-1">
        <div className="shrink-0 overflow-hidden size-20">
          <img
            alt={`zdjęcie poglądowe ćwiczenia ${item.name}`}
            className="w-full h-full aspect-square object-cover rounded-md bg-primary/5 dark:grayscale-[0.2] dark:contrast-110"
            src={item.image}
          />
        </div>
        <div className="flex flex-col justify-between min-h-20 sm:h-auto flex-1 min-w-0">
          <Typography className="text-sm sm:text-base font-semibold line-clamp-2 sm:line-clamp-1">
            {item.name}
          </Typography>
          <Typography className="text-xs sm:text-sm line-clamp-1" variant="muted">
            {displayMuscleGroups}
          </Typography>
          <div className="flex gap-1 sm:gap-2 items-center flex-wrap">
            <AdvancedLevelBadge advancedLevel={item.advancedLevel} />
            <LikesBadge likes={item.likes} />
          </div>
        </div>
      </div>

      <div className="flex gap-2 sm:flex-col justify-end sm:justify-start">
        <DetailsActionBtn
          className='"sm:w-auto w-full flex-1 sm:flex-none"'
          redirectTo={`/exercises/${item._id}`}
        />

        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              className="relative max-sm:w-full max-sm:flex-1"
              onClick={() => handleExerciseToggle(item._id)}
              size="icon"
              variant={isAdded ? 'destructive' : 'outline'}
            >
              <Plus
                className={cn(
                  'absolute size-4 scale-100 rotate-0 transition-all duration-200',
                  isAdded && 'scale-0 rotate-90',
                )}
              />
              <Trash2
                className={cn(
                  'absolute size-4 scale-0 rotate-90 transition-all duration-200',
                  isAdded && 'scale-100 rotate-0',
                )}
              />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <Typography variant="small">{isAdded ? 'Usuń z dnia' : 'Dodaj do dnia'}</Typography>
          </TooltipContent>
        </Tooltip>
      </div>
    </div>
  );
};

export default ExerciseItem;
