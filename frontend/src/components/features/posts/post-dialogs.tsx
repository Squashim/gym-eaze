import type { PostSchema } from '@/schemas/post-schema';
import { useForm, useFormContext } from 'react-hook-form';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { TagsInput } from '@/components/ui/tags-input';
import { Button } from '@/components/ui/button';
import type { GetPostsApiResponse } from '@/types/post';
import PostCreator from './post-creator';
import { Loader2, Pencil } from 'lucide-react';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import Typography from '@/components/ui/typography';
import { useComment } from '@/hooks/query/comments/use-comment';
import { useUser } from '@/context/user';
import type { GetCommentsApiResponse } from '@/types/comment';
import { updateCommentSchema, type UpdateCommentSchema } from '@/schemas/comment-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { Textarea } from '@/components/ui/textarea';
import { useEffect, useMemo, useRef } from 'react';
import type { ItemType } from '@/constants/item-type';

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

interface EditCommentDialogProps {
  open: boolean;
  parentId: string;
  parentType: ItemType;
  comment: GetCommentsApiResponse;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

const EditCommentDialog = ({
  open,
  comment,
  parentId,
  parentType,
  onOpenChange,
  onClose,
}: EditCommentDialogProps) => {
  const { user } = useUser();
  const { updateComment } = useComment({
    parentType: parentType,
    parentId: parentId,
  });
  const textAreaRef = useRef<HTMLTextAreaElement | null>(null);

  const form = useForm<UpdateCommentSchema>({
    resolver: zodResolver(updateCommentSchema),
    defaultValues: {
      content: '',
    },
  });

  const isReply = useMemo(() => {
    return parentType === 'Komentarz';
  }, [parentType]);

  useEffect(() => {
    if (!comment || !open) return;

    form.reset({
      content: comment.content,
    });

    requestAnimationFrame(() => {
      const textarea = textAreaRef.current;
      if (textarea) {
        textarea.focus();
        textarea.setSelectionRange(textarea.value.length, textarea.value.length);
      }
    });
  }, [form, comment, open]);

  const onSubmit = (data: UpdateCommentSchema) => {
    updateComment.mutate(
      { id: comment._id, payload: data },
      {
        onSuccess: () => {
          onClose();
        },
      },
    );
  };

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden" onInteractOutside={(e) => e.preventDefault()}>
        <DialogHeader>
          <DialogTitle>{!isReply ? 'Edytuj swój komentarz' : 'Edytuj swoją odpowiedź'}</DialogTitle>
          <DialogDescription>
            {!isReply
              ? 'Wprowadź zmiany w swoim komentarzu i zapisz je.'
              : 'Wprowadź zmiany w swojej odpowiedzi i zapisz je.'}
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form className="flex flex-col gap-2" onSubmit={form.handleSubmit(onSubmit)}>
            <div className="flex items-start gap-2 w-full">
              <ReusableAvatar
                className="shadow-sm size-10"
                profilePicture={user?.profileDetails.profilePicture}
                username={user?.username}
              />
              <FormField
                control={form.control}
                name="content"
                render={({ field }) => (
                  <FormItem className="flex-1">
                    <FormControl>
                      <Textarea
                        {...field}
                        autoComplete="off"
                        className="resize-none dark:bg-card bg-card max-h-24 scrollbar overflow-y-auto break-words"
                        maxLength={1000}
                        ref={textAreaRef}
                      />
                    </FormControl>
                    <div className="flex justify-between items-center min-h-5">
                      <FormMessage />
                      <Typography className="text-xs text-muted-foreground ml-auto" variant="small">
                        {field.value?.length ?? 0}/1000
                      </Typography>
                    </div>
                  </FormItem>
                )}
              />
            </div>

            <div className="ml-auto flex items-center gap-2">
              <Button onClick={onClose} type="button" variant="outline">
                Anuluj
              </Button>

              <Button
                disabled={
                  !form.formState.isValid || updateComment.isPending || !form.formState.isDirty
                }
                type="submit"
              >
                {updateComment.isPending ? (
                  <>
                    <Loader2 className="animate-spin" />
                    Edytowanie...
                  </>
                ) : (
                  <>
                    <Pencil />
                    {!isReply ? 'Edytuj komentarz' : 'Edytuj odpowiedź'}
                  </>
                )}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export { HashtagDialog, EditPostDialog, EditCommentDialog };
