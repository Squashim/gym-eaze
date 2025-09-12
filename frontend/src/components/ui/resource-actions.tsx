import { cn } from '@/lib/utils';
import { useState } from 'react';
import {
  DeleteActionBtn,
  EditActionBtn,
  FavoriteActionBtn,
  LikeActionBtn,
  ReportActionBtn,
} from './reusable-buttons';
import ReusableAlertDialog from './reusable-alert-dialog';

type ResourceActionsProps = {
  isAuthor: boolean;
  like?: {
    isLiked: boolean;
    isLikeLoading: boolean;
    onLike: () => void;
  };
  favorite?: {
    isFavorited: boolean;
    isFavoriteLoading: boolean;
    onFavorite: () => void;
  };
  edit?: {
    redirectTo?: string;
    onEdit?: () => void;
  };
  delete?: {
    onDelete: () => void;
    isDeleteLoading: boolean;
    deleteTitle: string;
    deleteDescription: string;
  };
  report?: {
    onReport: () => void;
  };
  className?: string;
};

const ResourceActions = ({
  isAuthor,
  like,
  favorite,
  edit,
  delete: deleteAction,
  report,
  className,
}: ResourceActionsProps) => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  return (
    <>
      {deleteAction && isAuthor && (
        <ReusableAlertDialog
          cancelLabel="Anuluj"
          confirmLabel="Usuń"
          description={deleteAction.deleteDescription}
          isLoading={deleteAction.isDeleteLoading}
          onClose={() => setIsDialogOpen(false)}
          onConfirm={deleteAction.onDelete}
          open={isDialogOpen}
          title={deleteAction.deleteTitle}
          variant="destructive"
        />
      )}
      <div className={cn('flex gap-2', className)}>
        {like && (
          <LikeActionBtn
            isActive={like.isLiked}
            isLoading={like.isLikeLoading}
            onClick={like.onLike}
          />
        )}
        {favorite && !isAuthor && (
          <FavoriteActionBtn
            isActive={favorite.isFavorited}
            isLoading={favorite.isFavoriteLoading}
            onClick={favorite.onFavorite}
          />
        )}
        {report && !isAuthor && <ReportActionBtn onClick={report.onReport} />}
        {edit && isAuthor && <EditActionBtn onClick={edit.onEdit} redirectTo={edit.redirectTo} />}
        {deleteAction && isAuthor && (
          <DeleteActionBtn
            onClick={() => {
              setIsDialogOpen(true);
            }}
          />
        )}
      </div>
    </>
  );
};

export default ResourceActions;
