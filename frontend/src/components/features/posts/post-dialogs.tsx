import type { PostSchema } from '@/schemas/post-schema';
import { useFormContext } from 'react-hook-form';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { FormControl, FormField, FormItem } from '@/components/ui/form';
import { TagsInput } from '@/components/ui/tags-input';
import { Button } from '@/components/ui/button';
import type { GetPostsApiResponse } from '@/types/post';
import PostCreator from './post-creator';

interface HashtagDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

const HashtagDialog = ({ open, onOpenChange, onClose }: HashtagDialogProps) => {
  const { control } = useFormContext<PostSchema>();

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden">
        <DialogHeader>
          <DialogTitle>Dodaj hasztagi</DialogTitle>
          <DialogDescription>
            Wpisane hasztagi pomogą innym użytkownikom znaleźć Twój post. Możesz dodać do 50
            hasztagów.
          </DialogDescription>
        </DialogHeader>

        <FormField
          control={control}
          name="hashtags"
          render={({ field }) => (
            <FormItem>
              <FormControl>
                <TagsInput
                  className="max-h-40"
                  id={field.name}
                  onChange={field.onChange}
                  placeholder="Dodaj hashtagi, np. biceps, triceps - użyj Entera lub przecinka"
                  value={field.value}
                />
              </FormControl>
            </FormItem>
          )}
        />

        <div className="flex items-center justify-end gap-2 mt-2">
          <Button
            onClick={onClose}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                e.preventDefault();
                onClose();
              }
            }}
            type="button"
            variant="outline"
          >
            Zamknij
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

interface EditPostDialogProps {
  open: boolean;
  post: GetPostsApiResponse;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

const EditPostDialog = ({ open, post, onOpenChange, onClose }: EditPostDialogProps) => {
  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden" onInteractOutside={(e) => e.preventDefault()}>
        <DialogHeader>
          <DialogTitle>Edytuj swój post</DialogTitle>
          <DialogDescription>Wprowadź zmiany w swoim poście i zapisz je.</DialogDescription>
        </DialogHeader>

        <PostCreator mode="edit" onEditCancel={onClose} post={post} />
      </DialogContent>
    </Dialog>
  );
};

export { HashtagDialog, EditPostDialog };
