import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { X } from 'lucide-react';

interface PostMediaPreviewProps {
  type: 'image' | 'video';
  src: string;
  author: string;
}

const PostMediaPreview = ({ type, src, author }: PostMediaPreviewProps) => {
  return (
    <Dialog>
      <DialogTrigger asChild>
        <div
          className="h-96 aspect-video rounded-xl overflow-hidden relative bg-primary/5 cursor-pointer"
          title="Kliknij, aby powiększyć"
        >
          {type === 'image' ? (
            <img
              alt={`zdjęcie do postu użytkownika ${author}`}
              className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
              loading="lazy"
              src={src}
            />
          ) : (
            <video
              className="w-full h-full object-contain rounded-xl dark:grayscale-[0.2] dark:contrast-110"
              controls
              src={src}
            >
              Twoja przeglądarka nie obsługuje odtwarzania filmów.
            </video>
          )}
        </div>
      </DialogTrigger>
      <DialogContent className="max-w-7xl border-0 bg-transparent shadow-none [&>button]:hidden">
        <DialogHeader>
          <DialogTitle className="sr-only">
            {type === 'image' ? 'Zdjęcie' : 'Filmik'} do postu użytkownika {author}
          </DialogTitle>
          <DialogClose asChild className="ml-auto">
            <Button size="icon">
              <X />
            </Button>
          </DialogClose>
        </DialogHeader>
        <div className="relative h-[calc(100vh-220px)] w-full overflow-clip rounded-md bg-transparent shadow-md">
          {type === 'image' ? (
            <img
              alt={`zdjęcie do postu użytkownika ${author}`}
              className="h-full w-full object-contain"
              loading="lazy"
              src={src}
            />
          ) : (
            <video className="w-full h-full object-contain" controls src={src}>
              Twoja przeglądarka nie obsługuje odtwarzania filmów.
            </video>
          )}
        </div>
        <DialogDescription className="sr-only">
          {`Pełny widok ${type === 'image' ? 'zdjęcia' : 'filmiku'} do postu użytkownika ${author}`}
        </DialogDescription>
      </DialogContent>
    </Dialog>
  );
};

export default PostMediaPreview;
