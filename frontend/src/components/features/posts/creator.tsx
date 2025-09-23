import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { TagsInput } from '@/components/ui/tags-input';
import { Textarea } from '@/components/ui/textarea';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { useUser } from '@/context/user';
import { usePost } from '@/hooks/query/posts/use-post';
import useOpen from '@/hooks/utils/use-open';
import { postSchema, type PostSchema } from '@/schemas/post-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { Hash, Image, Trash2, Video } from 'lucide-react';
import { useCallback, useRef } from 'react';
import { useForm, useFormContext } from 'react-hook-form';

function PostCreator() {
  const { user } = useUser();
  const hashtagDialog = useOpen();
  const { createPost } = usePost();
  const imageRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLInputElement>(null);

  const form = useForm<PostSchema>({
    resolver: zodResolver(postSchema),
    defaultValues: {
      content: '',
      image: undefined,
      video: undefined,
      hashtags: [],
    },
  });

  const { watch, setValue } = form;
  const imageFile = watch('image');
  const videoFile = watch('video');
  const hashtags = watch('hashtags');

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, type: 'image' | 'video') => {
    form.setValue(type, e.target.files?.[0]);
  };

  const handleRemoveFile = useCallback(
    (type: 'image' | 'video') => {
      setValue(type, undefined);
      if (type === 'image' && imageRef.current) imageRef.current.value = '';
      if (type === 'video' && videoRef.current) videoRef.current.value = '';
    },
    [setValue],
  );

  const onSubmit = (data: PostSchema) => {
    createPost.mutate(data, {
      onSuccess: () => {
        form.reset();
        if (imageRef.current) imageRef.current.value = '';
        if (videoRef.current) videoRef.current.value = '';
      },
    });
  };

  return (
    <Form {...form}>
      <form
        className="bg-card text-card-foreground gap-4 rounded-xl border p-4 shadow-sm flex max-w-xl"
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <ReusableAvatar className="size-12" fallbackClassName="dark:bg-background" variant="h4" />
        <div className="flex flex-col gap-2 w-full min-w-0">
          <FormField
            control={form.control}
            name="content"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Textarea
                    {...field}
                    autoComplete="off"
                    autoFocus
                    className="resize-none dark:bg-card bg-card min-h-24 max-h-48 scrollbar overflow-y-auto break-all"
                    maxLength={800}
                    placeholder={`Co słychać${user ? ', ' + user.username : ''}?`}
                  />
                </FormControl>
                <div className="flex justify-between items-center min-h-5">
                  <FormMessage />
                  <Typography className="text-xs text-muted-foreground ml-auto" variant="small">
                    {field.value?.length ?? 0}/800
                  </Typography>
                </div>
              </FormItem>
            )}
          />

          {imageFile && (
            <UploadedFile file={imageFile} onFileRemove={handleRemoveFile} type="image" />
          )}

          {videoFile && (
            <UploadedFile file={videoFile} onFileRemove={handleRemoveFile} type="video" />
          )}

          <div className="flex items-center justify-between flex-wrap gap-2">
            <div className="flex gap-2 border rounded-md bg-muted">
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    datatype="file"
                    disabled={!!videoFile}
                    onClick={() => imageRef.current?.click()}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        imageRef.current?.click();
                      }
                    }}
                    size="icon"
                    type="button"
                    variant="ghost"
                  >
                    <Image />
                    <input
                      accept="image/*"
                      className="hidden"
                      onChange={(e) => handleFileChange(e, 'image')}
                      ref={imageRef}
                      type="file"
                    />
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  <Typography variant="small">Dołącz zdjęcie</Typography>
                </TooltipContent>
              </Tooltip>

              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    datatype="file"
                    disabled={!!imageFile}
                    onClick={() => videoRef.current?.click()}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        videoRef.current?.click();
                      }
                    }}
                    size="icon"
                    type="button"
                    variant="ghost"
                  >
                    <Video />
                    <input
                      accept="video/*"
                      className="hidden"
                      onChange={(e) => handleFileChange(e, 'video')}
                      ref={videoRef}
                      type="file"
                    />
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  <Typography variant="small">Dołącz filmik</Typography>
                </TooltipContent>
              </Tooltip>

              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    className="relative"
                    onClick={hashtagDialog.open}
                    size="icon"
                    type="button"
                    variant="ghost"
                  >
                    <Hash />
                    {hashtags.length > 0 && (
                      <Badge
                        className="absolute -top-1 -right-1 rounded-full min-w-5 h-5 px-1 tabular-nums"
                        variant="secondary"
                      >
                        {hashtags.length}
                      </Badge>
                    )}
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  <Typography variant="small">Dodaj hasztagi</Typography>
                </TooltipContent>
              </Tooltip>
            </div>
            <Button disabled={!form.formState.isValid || createPost.isPending}>
              {createPost.isPending ? 'Dodawanie...' : 'Dodaj post'}
            </Button>
          </div>
        </div>
      </form>

      <HashtagDialog
        onClose={hashtagDialog.close}
        onOpenChange={hashtagDialog.setIsOpen}
        open={hashtagDialog.isOpen}
      />
    </Form>
  );
}

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
            <FormItem className="md:col-span-2">
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

interface UploadedFileProps {
  file: File;
  onFileRemove: (type: 'image' | 'video') => void;
  type: 'image' | 'video';
}

const UploadedFile = ({ file, onFileRemove, type }: UploadedFileProps) => {
  return (
    <div className="flex items-center gap-2 rounded-md bg-muted/50 p-2 border">
      <div className="rounded-md bg-background p-2">
        {type === 'video' ? (
          <Video className="size-4 text-muted-foreground" />
        ) : (
          <Image className="size-4 text-muted-foreground" />
        )}
      </div>
      <div className="flex-1 min-w-0">
        <p className="truncate text-sm font-medium">{file.name}</p>
        <p className="text-xs text-muted-foreground">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
      </div>
      <Button
        className="flex-none size-8"
        onClick={() => onFileRemove(type)}
        size="icon"
        type="button"
        variant="ghost"
      >
        <Trash2 className="size-4 text-muted-foreground hover:text-destructive" />
        <span className="sr-only">Usuń plik</span>
      </Button>
    </div>
  );
};

export default PostCreator;
