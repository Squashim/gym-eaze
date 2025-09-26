import { Button } from '@/components/ui/button';
import { Image, Trash2, Video } from 'lucide-react';

interface PostUploadedFileProps {
  file: File;
  onFileRemove: (type: 'image' | 'video') => void;
  type: 'image' | 'video';
}

const PostUploadedFile = ({ file, onFileRemove, type }: PostUploadedFileProps) => {
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

export default PostUploadedFile;
