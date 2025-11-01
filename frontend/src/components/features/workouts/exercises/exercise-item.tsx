import { Badge } from '@/components/ui/badge';
import { DetailsActionBtn } from '@/components/ui/reusable-buttons';
import Typography from '@/components/ui/typography';
import { parseSecondsToTimeString, parseWorkoutSetsLabel } from '@/lib/parsers';
import type { GetExercisesByIdsApiResponse } from '@/types/exercise';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { GripVertical } from 'lucide-react';
import ExerciseManageDropdown from './exercise-manage-dropdown';
import { useCallback } from 'react';
import type { WorkoutApiExercise } from '@/types/workout';
import { isDefaultExercise } from '@/lib/utils';

interface ExerciseItemProps {
  exercise: GetExercisesByIdsApiResponse | WorkoutApiExercise;
  setsCount: number;
  exercisesCount?: number;
  restAfterSets: number;
  onExerciseRemove: (id: string) => void;
  onSelect: (id: string | null) => void;
  onDialogOpen: (exerciseId: string) => void;
  isLastExercise: boolean;
  id: string;
}

const ExerciseItem = ({
  exercise,
  exercisesCount,
  onExerciseRemove,
  onDialogOpen,
  onSelect,
  id,
  restAfterSets,
  isLastExercise,
  setsCount,
}: ExerciseItemProps) => {
  const displayMuscleGroups = exercise.muscleGroup.map((mg) => mg.name).join(' • ');

  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  const handleDelete = useCallback(() => {
    onExerciseRemove(isDefaultExercise(exercise) ? exercise.originalId : exercise._id);
  }, [onExerciseRemove, exercise]);

  const handleDialogOpen = useCallback(() => {
    onDialogOpen(isDefaultExercise(exercise) ? exercise.originalId : exercise._id);
  }, [onDialogOpen, exercise]);

  const handleSelect = useCallback(() => {
    onSelect(isDefaultExercise(exercise) ? exercise.originalId : exercise._id);
  }, [onSelect, exercise]);

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        onSelect(isDefaultExercise(exercise) ? exercise.originalId : exercise._id);
      }
    },
    [onSelect, exercise],
  );

  return (
    <div
      className="rounded-md p-3 shadow-md flex items-center gap-2 border bg-card text-card-foreground hover:bg-primary/10 cursor-pointer transition-[background,border] ease-in-out duration-200 hover:border-primary/30 relative min-[425px]:h-24 flex-wrap  min-[425px]:flex-nowrap"
      onClick={handleSelect}
      onKeyDown={handleKeyDown}
      ref={setNodeRef}
      role="button"
      style={style}
      tabIndex={0}
    >
      <div {...listeners} {...attributes} className="cursor-grab" tabIndex={-1}>
        <GripVertical className="size-5" />
      </div>

      <div className="flex-shrink-0 overflow-hidden h-full aspect-square max-h-24">
        <img
          alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
          className="w-full h-full aspect-square object-cover rounded-md bg-primary/5 dark:grayscale-[0.2] dark:contrast-110"
          src={exercise.image}
        />
      </div>

      <div className="flex flex-col items-start justify-between min-[425px]:h-full">
        <Typography className="text-sm font-semibold line-clamp-1">{exercise.name}</Typography>
        <Typography className="text-sm line-clamp-1" variant="muted">
          {displayMuscleGroups}
        </Typography>
        <div className="flex items-center gap-1">
          <Badge variant="accent">{parseWorkoutSetsLabel(setsCount)}</Badge>
          {exercisesCount && !(isLastExercise && setsCount === 1) && (
            <Badge className="justify-start" variant="secondary">
              przerwa {parseSecondsToTimeString(restAfterSets)}
            </Badge>
          )}
        </div>
      </div>

      <div className="ml-auto flex flex-col justify-between absolute top-4 right-4 min-[425px]:h-full min-[425px]:static">
        <ExerciseManageDropdown
          exercisesCount={exercisesCount}
          onDelete={handleDelete}
          onDialogOpen={handleDialogOpen}
        />

        <DetailsActionBtn
          redirectTo={`/exercises/${isDefaultExercise(exercise) ? exercise.originalId : exercise._id}`}
        />
      </div>
    </div>
  );
};

export default ExerciseItem;
