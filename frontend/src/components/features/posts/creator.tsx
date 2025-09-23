import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Textarea } from '@/components/ui/textarea';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { useUser } from '@/context/user';
import { postSchema, type PostSchema } from '@/schemas/post-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { Hash, Image, Trash2, Video } from 'lucide-react';
import { useCallback, useRef } from 'react';
import { useForm } from 'react-hook-form';

function PostCreator() {
  const { user } = useUser();
  // const hashtagDialog = useOpen();
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
    console.log(data);
  };

  return (
    <Form {...form}>
      <form
        className="bg-card text-card-foreground gap-6 rounded-xl border p-4 shadow-sm flex max-w-xl"
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <div>{user?.username}</div>
        <div className="flex flex-col gap-2 flex-1">
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

          <div className="flex items-center justify-between">
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
                  <Button size="icon" type="button" variant="ghost">
                    <Hash />
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  <Typography variant="small">Dodaj hasztagi</Typography>
                </TooltipContent>
              </Tooltip>
            </div>
            <Button disabled={!form.formState.isValid}>Dodaj post</Button>
          </div>
        </div>
      </form>
    </Form>
  );
}

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
