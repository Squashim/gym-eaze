import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { CopyPlus, EllipsisVertical, Trash2 } from 'lucide-react';

interface ExerciseManageDropdownProps {
  exercisesCount?: number;
  onDelete: () => void;
  onDialogOpen: () => void;
}

function ExerciseManageDropdown({
  exercisesCount,
  onDelete,
  onDialogOpen,
}: ExerciseManageDropdownProps) {
  return (
    <>
      <DropdownMenu>
        <Tooltip>
          <TooltipTrigger asChild>
            <DropdownMenuTrigger asChild tabIndex={0}>
              <Button
                onClick={(e) => e.stopPropagation()}
                onKeyDown={(e) => e.stopPropagation()}
                size="icon"
                variant="ghost"
              >
                <EllipsisVertical />
              </Button>
            </DropdownMenuTrigger>
          </TooltipTrigger>
          <TooltipContent>
            <Typography variant="small">Więcej opcji ćwiczenia</Typography>
          </TooltipContent>
        </Tooltip>
        <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
          <DropdownMenuItem
            disabled={exercisesCount === 1}
            onClick={(e) => {
              e.stopPropagation();
              onDialogOpen();
            }}
            onKeyDown={(e) => e.stopPropagation()}
          >
            <CopyPlus />
            Skopiuj dane z tego ćwiczenia
          </DropdownMenuItem>
          <DropdownMenuItem
            onClick={(e) => {
              e.stopPropagation();
              onDelete();
            }}
            onKeyDown={(e) => e.stopPropagation()}
            variant="destructive"
          >
            <Trash2 />
            Usuń ćwiczenie z dnia
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </>
  );
}

export default ExerciseManageDropdown;
