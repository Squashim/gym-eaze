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

interface DayManageDropdownProps {
  daysCount: number;
  exercisesCount?: number;
  onDelete: () => void;
  onDialogOpen: () => void;
}

function DayManageDropdown({
  daysCount,
  exercisesCount,
  onDelete,
  onDialogOpen,
}: DayManageDropdownProps) {
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
            <Typography variant="small">Więcej opcji dnia</Typography>
          </TooltipContent>
        </Tooltip>
        <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
          <DropdownMenuItem
            disabled={daysCount === 1 || exercisesCount === 0}
            onClick={(e) => {
              e.stopPropagation();
              onDialogOpen();
            }}
            onKeyDown={(e) => e.stopPropagation()}
          >
            <CopyPlus />
            Skopiuj dane z tego dnia
          </DropdownMenuItem>
          <DropdownMenuItem
            disabled={daysCount === 1}
            onClick={(e) => {
              e.stopPropagation();
              onDelete();
            }}
            onKeyDown={(e) => e.stopPropagation()}
            variant="destructive"
          >
            <Trash2 />
            Usuń dzień
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </>
  );
}

export default DayManageDropdown;
