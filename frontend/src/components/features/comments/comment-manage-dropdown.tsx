import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { EllipsisVertical, Flag, Pencil, Trash2 } from 'lucide-react';

interface CommentManageDropdownProps {
  isAuthor: boolean;
  onDelete: () => void;
  onEdit: () => void;
  onReport: () => void;
  type: 'comment' | 'reply';
  isOpen: boolean | null;
  onOpenChange: (isOpen: boolean | null) => void;
}

const CommentManageDropdown = ({
  isAuthor,
  onDelete,
  onEdit,
  onReport,
  type,
  isOpen,
  onOpenChange,
}: CommentManageDropdownProps) => {
  return (
    <DropdownMenu onOpenChange={onOpenChange} open={!!isOpen}>
      <Tooltip>
        <TooltipTrigger asChild>
          <DropdownMenuTrigger asChild>
            <Button className="size-7" size="icon" variant="ghost">
              <EllipsisVertical />
            </Button>
          </DropdownMenuTrigger>
        </TooltipTrigger>
        <TooltipContent>
          <Typography variant="small">
            Więcej opcji {type === 'comment' ? 'komentarza' : 'odpowiedzi'}
          </Typography>
        </TooltipContent>
      </Tooltip>
      <DropdownMenuContent align="start">
        {isAuthor ? (
          <>
            <DropdownMenuItem onClick={onEdit}>
              <Pencil />
              Edytuj {type === 'comment' ? 'komentarz' : 'odpowiedź'}
            </DropdownMenuItem>
            <DropdownMenuItem onClick={onDelete} variant="destructive">
              <Trash2 />
              Usuń {type === 'comment' ? 'komentarz' : 'odpowiedź'}
            </DropdownMenuItem>
          </>
        ) : (
          <DropdownMenuItem onClick={onReport}>
            <Flag />
            Zgłoś {type === 'comment' ? 'komentarz' : 'odpowiedź'}
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default CommentManageDropdown;
