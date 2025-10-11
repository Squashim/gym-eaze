import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Skeleton } from '@/components/ui/skeleton';
import { Textarea } from '@/components/ui/textarea';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { useUser } from '@/context/user';
import { usePost } from '@/hooks/query/posts/use-post';
import useFormFiles from '@/hooks/form/use-form-files';
import useOpen from '@/hooks/utils/use-open';
import { cn, extractDirtyFieldsData } from '@/lib/utils';
import { postSchema, type PostSchema } from '@/schemas/post-schema';
import type { GetPostsApiResponse, UpdatePostPayload } from '@/types/post';
import { zodResolver } from '@hookform/resolvers/zod';
import { Hash, Image, Video } from 'lucide-react';
import { useCallback, useEffect, useMemo, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { HashtagDialog } from './post-dialogs';
import PostUploadedFile from './post-uploaded-file';
import { postCreatorDefaultValues } from '@/constants/defaults/forms';

interface PostCreatorProps {
  mode: 'edit' | 'create';
  post?: GetPostsApiResponse;
  onEditCancel?: () => void;
}

function PostCreator({ mode, post, onEditCancel }: PostCreatorProps) {
  const { user } = useUser();
  const hashtagDialog = useOpen();
  const { createPost, updatePost } = usePost();
  const imageRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLInputElement>(null);
  const form = useForm<PostSchema>({
    resolver: zodResolver(postSchema),
    defaultValues: postCreatorDefaultValues,
  });
  const { watch, setValue } = form;
  const imageFile = watch('image');
  const videoFile = watch('video');
  const hashtags = watch('hashtags');
  const isEditMode = useMemo(() => {
    return mode === 'edit';
  }, [mode]);

  const {
    imageFile: defaultImageFile,
    videoFile: defaultVideoFile,
    isLoading: isFilesLoading,
    compareFiles,
  } = useFormFiles({
    imageUrl: post?.image,
    videoUrl: post?.video,
    enabled: isEditMode && Boolean(post),
  });

  const { isImageDefault, isVideoDefault } = useMemo(() => {
    return compareFiles(imageFile, videoFile);
  }, [imageFile, videoFile, compareFiles]);

  useEffect(() => {
    if (!post || !isEditMode || isFilesLoading) return;

    const fillForm = () => {
      form.reset({
        content: post.content,
        hashtags: post.hashtags || [],
        image: defaultImageFile,
        video: defaultVideoFile,
      });
    };

    void fillForm();
  }, [form, isEditMode, post, isFilesLoading, defaultImageFile, defaultVideoFile]);

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

  const hasMediaChanged = () => {
    const imageChanged = defaultImageFile
      ? isImageDefault === false || isImageDefault === undefined
      : !!imageFile;
    const videoChanged = defaultVideoFile
      ? isVideoDefault === false || isVideoDefault === undefined
      : !!videoFile;
    return imageChanged || videoChanged;
  };

  const handleCreate = (data: PostSchema) => {
    createPost.mutate(data, {
      onSuccess: () => {
        form.reset();
        if (imageRef.current) imageRef.current.value = '';
        if (videoRef.current) videoRef.current.value = '';
      },
    });
  };

  const handleUpdate = (data: PostSchema) => {
    const dirtyFields = form.formState.dirtyFields;

    const formattedData = extractDirtyFieldsData<PostSchema, UpdatePostPayload>(dirtyFields, data);

    if (defaultImageFile) {
      if (isImageDefault === false) {
        formattedData.image = imageFile;
      } else if (isImageDefault === undefined) {
        formattedData.image = null;
      }
    } else if (imageFile) {
      formattedData.image = imageFile;
    }

    if (defaultVideoFile) {
      if (isVideoDefault === false) {
        formattedData.video = videoFile;
      } else if (isVideoDefault === undefined) {
        formattedData.video = null;
      }
    } else if (videoFile) {
      formattedData.video = videoFile;
    }

    updatePost.mutate(
      { id: post!._id, payload: formattedData },
      {
        onSuccess: () => {
          if (onEditCancel) onEditCancel();
        },
      },
    );
  };

  const onSubmit = (data: PostSchema) => {
    if (isEditMode) {
      handleUpdate(data);
    } else {
      handleCreate(data);
    }
  };

  return (
    <Form {...form}>
      <form
        className={cn(
          'gap-4 rounded-xl shadow-sm flex w-full min-w-0',
          isEditMode
            ? 'bg-transparent text-popover-foreground'
            : 'bg-card text-card-foreground border p-4',
        )}
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <ReusableAvatar
          className="size-12"
          fallbackClassName="dark:bg-background"
          profilePicture={user?.profileDetails.profilePicture}
          username={user?.username}
          variant="h4"
        />
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
                    className="resize-none dark:bg-card bg-card min-h-24 max-h-48 scrollbar overflow-y-auto break-words"
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

          {isFilesLoading && isEditMode ? (
            <Skeleton className="h-8 p-2 flex items-center justify-start">
              <Typography className="text-sm truncate">Ładowanie załączników...</Typography>
            </Skeleton>
          ) : (
            <>
              {imageFile && (
                <PostUploadedFile file={imageFile} onFileRemove={handleRemoveFile} type="image" />
              )}

              {videoFile && (
                <PostUploadedFile file={videoFile} onFileRemove={handleRemoveFile} type="video" />
              )}
            </>
          )}

          <div className="flex items-center justify-between flex-wrap gap-2">
            <div className="flex gap-2 border rounded-md bg-muted">
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    datatype="file"
                    disabled={!!videoFile || (isEditMode && isFilesLoading)}
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
                      accept="image/jpeg, image/webp, image/png"
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
                    disabled={!!imageFile || (isEditMode && isFilesLoading)}
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
                      accept="video/mp4, video/webm"
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

            <div className="flex items-center gap-2">
              {isEditMode && (
                <Button onClick={onEditCancel} type="button" variant="outline">
                  Anuluj
                </Button>
              )}
              <Button
                disabled={
                  createPost.isPending ||
                  updatePost.isPending ||
                  (isEditMode && isFilesLoading) ||
                  (isEditMode && !form.formState.isDirty && !hasMediaChanged())
                }
                type="submit"
              >
                {isEditMode
                  ? updatePost.isPending
                    ? 'Zapisywanie...'
                    : 'Zapisz zmiany'
                  : createPost.isPending
                    ? 'Publikowanie...'
                    : 'Opublikuj'}
              </Button>
            </div>
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

export default PostCreator;
