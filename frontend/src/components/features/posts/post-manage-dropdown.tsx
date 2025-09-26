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

interface PostManageDropdownProps {
  isAuthor: boolean;
  onDelete: () => void;
  onEdit: () => void;
}

const PostManageDropdown = ({ isAuthor, onDelete, onEdit }: PostManageDropdownProps) => {
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
          <Typography variant="small">Więcej opcji posta</Typography>
        </TooltipContent>
      </Tooltip>
      <DropdownMenuContent align="end">
        {isAuthor ? (
          <>
            <DropdownMenuItem onClick={onEdit}>
              <Pencil />
              Edytuj post
            </DropdownMenuItem>
            <DropdownMenuItem onClick={onDelete} variant="destructive">
              <Trash2 />
              Usuń post
            </DropdownMenuItem>
          </>
        ) : (
          <DropdownMenuItem>
            <Flag />
            Zgłoś post
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default PostManageDropdown;
