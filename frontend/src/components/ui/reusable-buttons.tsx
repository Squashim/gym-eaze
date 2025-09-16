import { Button } from '@/components/ui/button';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { cn } from '@/lib/utils';
import { Flag, Heart, Loader2, Pencil, ThumbsUp, Trash } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

type BaseBtnProps = {
  onClick: () => void;
};

type EditActionBtnProps = Partial<BaseBtnProps> & {
  redirectTo?: string;
};

type ToggleBtnProps = BaseBtnProps & {
  isActive?: boolean;
  isLoading?: boolean;
};

const EditActionBtn = ({ redirectTo, onClick }: EditActionBtnProps) => {
  const navigate = useNavigate();

  const handleEdit = () => {
    if (redirectTo) {
      void navigate(redirectTo);
    } else if (onClick) {
      onClick();
    }
  };

  return (
    <Button onClick={handleEdit}>
      <Pencil />
      Edytuj
    </Button>
  );
};

const DeleteActionBtn = ({ onClick }: BaseBtnProps) => {
  return (
    <Button onClick={onClick} variant="destructive">
      <Trash />
      Usuń
    </Button>
  );
};

const LikeActionBtn = ({ onClick, isActive, isLoading }: ToggleBtnProps) => {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          disabled={isLoading}
          onClick={onClick}
          size="icon"
          variant={isActive ? 'secondary' : 'outline'}
        >
          {isLoading ? <Loader2 className="animate-spin" /> : <ThumbsUp />}
        </Button>
      </TooltipTrigger>
      <TooltipContent>
        <Typography variant="small">
          {isActive ? 'Cofnij polubienie' : 'Dodaj polubienie'}
        </Typography>
      </TooltipContent>
    </Tooltip>
  );
};

const FavoriteActionBtn = ({ onClick, isActive, isLoading }: ToggleBtnProps) => {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button disabled={isLoading} onClick={onClick} size="icon" variant="outline">
          {isLoading ? (
            <Loader2 className="animate-spin" />
          ) : (
            <Heart className={cn(isActive && 'fill-destructive text-destructive')} />
          )}
        </Button>
      </TooltipTrigger>
      <TooltipContent>
        <Typography variant="small">
          {isActive ? 'Usuń z ulubionych' : 'Dodaj do ulubionych'}
        </Typography>
      </TooltipContent>
    </Tooltip>
  );
};

const ReportActionBtn = ({ onClick }: BaseBtnProps) => {
  return (
    <>
      <Button onClick={onClick} variant="outline">
        <Flag />
        Zgłoś
      </Button>
    </>
  );
};

export { EditActionBtn, DeleteActionBtn, LikeActionBtn, FavoriteActionBtn, ReportActionBtn };
