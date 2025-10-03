import { Button } from '@/components/ui/button';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { cn } from '@/lib/utils';
import {
  Download,
  ExternalLink,
  Flag,
  Heart,
  Loader2,
  Pencil,
  ThumbsUp,
  Trash,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import ReusableAvatar from './reusable-avatar';

type BaseBtnProps = {
  onClick: () => void;
};

type RedirectBtnProps = Partial<BaseBtnProps> & {
  redirectTo?: string;
};

type ToggleBtnProps = BaseBtnProps & {
  isActive?: boolean;
  isLoading?: boolean;
};

type DetailsAuthorBtnProps = RedirectBtnProps & {
  authorName?: string;
  authorAvatar?: string;
  isAuthor: boolean;
};

const EditActionBtn = ({ redirectTo, onClick }: RedirectBtnProps) => {
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
    <Button onClick={onClick} variant="outline">
      <Flag />
      Zgłoś
    </Button>
  );
};

const DownloadActionBtn = ({ onClick, isLoading }: BaseBtnProps & { isLoading: boolean }) => {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button disabled={isLoading} onClick={onClick} size="icon" variant="outline">
          {isLoading ? <Loader2 className="animate-spin" /> : <Download />}
        </Button>
      </TooltipTrigger>
      <TooltipContent>
        <Typography variant="small">Pobierz plan (plik .pdf)</Typography>
      </TooltipContent>
    </Tooltip>
  );
};

const DetailsActionBtn = ({
  redirectTo,
  className,
}: RedirectBtnProps & {
  className?: string;
}) => {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLAnchorElement>) => {
    if (e.key === ' ') {
      e.preventDefault();
      if (redirectTo) window.open(redirectTo, '_blank', 'noreferrer');
    }
  };

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          asChild
          className={className}
          onClick={(e) => e.stopPropagation()}
          onKeyDown={(e) => e.stopPropagation()}
          size="icon"
          variant="ghost"
        >
          <a
            aria-label="Zobacz szczegóły"
            href={redirectTo}
            onKeyDown={handleKeyDown}
            rel="noreferrer"
            target="_blank"
          >
            <ExternalLink />
          </a>
        </Button>
      </TooltipTrigger>
      <TooltipContent>
        <Typography variant="small">Zobacz szczegóły</Typography>
      </TooltipContent>
    </Tooltip>
  );
};

const DetailsAuthorBtn = ({
  redirectTo,
  authorName,
  authorAvatar,
  isAuthor,
}: DetailsAuthorBtnProps) => {
  return (
    <div className="flex items-center gap-2 md:justify-end">
      <Button
        asChild
        className="flex items-center gap-2 p-0 hover:bg-transparent rounded-tl-2xl rounded-bl-2xl"
        variant="ghost"
      >
        <a
          className="flex items-center gap-2 pr-2"
          href={redirectTo}
          rel="noreferrer"
          target="_blank"
        >
          <ReusableAvatar
            className="shadow-sm"
            fallbackClassName="dark:bg-background"
            profilePicture={authorAvatar}
            username={authorName}
          />
          {authorName}
          {isAuthor && ' (Ty)'}
        </a>
      </Button>
    </div>
  );
};

export {
  EditActionBtn,
  DeleteActionBtn,
  LikeActionBtn,
  FavoriteActionBtn,
  ReportActionBtn,
  DownloadActionBtn,
  DetailsActionBtn,
  DetailsAuthorBtn,
};
