import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Textarea } from '@/components/ui/textarea';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import type { ItemType } from '@/constants/item-type';
import { useUser } from '@/context/user';
import { useComment } from '@/hooks/query/comments/use-comment';
import { cn } from '@/lib/utils';
import { commentSchema, type CommentSchema } from '@/schemas/comment-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { Loader2, Send, X } from 'lucide-react';
import { useForm } from 'react-hook-form';

interface CommentCreatorProps {
  parentType: ItemType;
  parentId: string;
  className?: string;
  scrollRef?: React.RefObject<HTMLDivElement | null>;
  replyingTo?: string | null;
  onClose?: () => void;
}

function CommentCreator({
  parentType,
  parentId,
  className,
  replyingTo,
  scrollRef,
  onClose,
}: CommentCreatorProps) {
  const { user } = useUser();
  const { createComment } = useComment({ parentType, parentId });
  const form = useForm<CommentSchema>({
    resolver: zodResolver(commentSchema),
    defaultValues: {
      parentType,
      parentId,
      content: '',
    },
  });

  const onSubmit = (data: CommentSchema) => {
    createComment.mutate(data, {
      onSuccess: () => {
        if (onClose) {
          onClose();
        }

        if (scrollRef?.current) {
          scrollRef.current?.scrollIntoView({ behavior: 'smooth' });
        }

        form.reset();
      },
    });
  };

  return (
    <Form {...form}>
      <form
        className={cn('flex flex-col gap-2 p-4', className)}
        onSubmit={form.handleSubmit(onSubmit)}
      >
        {replyingTo && (
          <div className="flex items-center justify-between gap-2">
            <Typography className="text-muted-foreground" variant="small">
              Odpowiadasz @{replyingTo}
            </Typography>
            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  className="size-8"
                  onClick={onClose}
                  size="icon"
                  type="button"
                  variant="outline"
                >
                  <X />
                </Button>
              </TooltipTrigger>
              <TooltipContent>
                <Typography variant="small">Anuluj</Typography>
              </TooltipContent>
            </Tooltip>
          </div>
        )}

        <div className="flex items-start gap-2 w-full">
          <ReusableAvatar
            className="shadow-sm size-10"
            profilePicture={user?.profileDetails.profilePicture}
            username={user?.username}
          />
          <div className="flex items-center gap-2 w-full min-w-0">
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem className="flex-1">
                  <FormControl>
                    <Textarea
                      {...field}
                      autoComplete="off"
                      autoFocus
                      className="resize-none dark:bg-card bg-card max-h-16 scrollbar overflow-y-auto break-words"
                      disabled={!user}
                      maxLength={1000}
                      placeholder={
                        parentType === 'Komentarz'
                          ? 'Napisz odpowiedź...'
                          : user
                            ? 'Napisz komentarz...'
                            : 'Zaloguj się, aby dodać komentarz...'
                      }
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

            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  className="ml-auto"
                  disabled={!form.formState.isValid || createComment.isPending || !user}
                  size="icon"
                  type="submit"
                >
                  {createComment.isPending ? <Loader2 className="animate-spin" /> : <Send />}
                </Button>
              </TooltipTrigger>
              <TooltipContent>
                <Typography variant="small">Skomentuj</Typography>
              </TooltipContent>
            </Tooltip>
          </div>
        </div>
      </form>
    </Form>
  );
}

export default CommentCreator;
