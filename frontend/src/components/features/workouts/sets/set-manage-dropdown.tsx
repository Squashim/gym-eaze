import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { WORKOUT } from '@/constants/workout';
import { CopyPlus, EllipsisVertical, Trash2 } from 'lucide-react';

interface SetManageDropdownProps {
  setsCount: number;
  onDuplicate: () => void;
  onDelete: () => void;
}

function SetManageDropdown({ setsCount, onDuplicate, onDelete }: SetManageDropdownProps) {
  return (
    <DropdownMenu>
      <Tooltip>
        <TooltipTrigger asChild>
          <DropdownMenuTrigger asChild tabIndex={0}>
            <Button size="icon" variant="ghost">
              <EllipsisVertical />
            </Button>
          </DropdownMenuTrigger>
        </TooltipTrigger>
        <TooltipContent>
          <Typography variant="small">Więcej opcji serii</Typography>
        </TooltipContent>
      </Tooltip>
      <DropdownMenuContent align="end">
        <DropdownMenuItem
          disabled={setsCount === WORKOUT.MAX_SETS_PER_EXERCISE}
          onClick={onDuplicate}
        >
          <CopyPlus /> Duplikuj serię
        </DropdownMenuItem>
        <DropdownMenuItem disabled={setsCount === 1} onClick={onDelete} variant="destructive">
          <Trash2 />
          Usuń serię
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

export default SetManageDropdown;
