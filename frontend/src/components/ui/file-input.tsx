import { cn } from '@/lib/utils';
import { FileImage, FilePlayIcon, Trash2, Upload } from 'lucide-react';
import { Button } from './button';
import { useRef, useState, type ChangeEvent, type DragEvent, type MouseEvent } from 'react';

interface FileInputProps {
  className?: string;
  value?: File | null;
  onChange?: (file: File | null) => void;
  disabled?: boolean;
  invalid?: boolean;
  accept?: string;
  id: string;
  variant?: 'video' | 'image';
}

const FileInput = ({
  className,
  value,
  onChange,
  disabled,
  accept,
  id,
  invalid,
  variant = 'image',
}: FileInputProps) => {
  const [isDragging, setIsDragging] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleFileSelection = (file: File | null) => {
    if (!disabled) onChange?.(file);
  };

  const handleDragEvents = (e: DragEvent<HTMLDivElement>, isOver: boolean) => {
    e.preventDefault();
    if (!disabled) setIsDragging(isOver);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);

    if (!disabled) {
      const file = e.dataTransfer.files?.[0] ?? null;
      handleFileSelection(file);
    }
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    handleFileSelection(e.target.files?.[0] ?? null);
  };

  const handleRemove = (e: MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    handleFileSelection(null);
    if (inputRef.current) inputRef.current.value = '';
  };

  return (
    <div className={cn('space-y-2 min-w-0', className)}>
      <div
        aria-disabled={disabled}
        className={cn(
          'relative cursor-pointer rounded-md border border-dashed border-muted-foreground/25 px-6 py-8 text-center transition-colors hover:bg-muted/50',
          isDragging && 'border-muted-foreground/50 bg-muted/50',
          disabled && 'cursor-not-allowed opacity-60',
          invalid && 'border-destructive',
        )}
        data-type="file"
        onClick={() => inputRef.current?.click()}
        onDragLeave={(e) => handleDragEvents(e, false)}
        onDragOver={(e) => handleDragEvents(e, true)}
        onDrop={handleDrop}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            inputRef.current?.click();
          }
        }}
        role="button"
        tabIndex={0}
      >
        <input
          accept={accept}
          className="hidden"
          disabled={disabled}
          id={id}
          onChange={handleChange}
          ref={inputRef}
          type="file"
        />
        <div className="flex flex-col items-center gap-1">
          <Upload className="h-8 w-8 text-muted-foreground" />

          <p className="text-sm text-muted-foreground">
            <span className="font-semibold text-primary">Kliknij, aby przesłać</span> lub
            przeciągnij i upuść
          </p>
        </div>
      </div>
      {value && (
        <div className="flex items-center gap-2 rounded-md bg-muted/50 p-2">
          <div className="rounded-md bg-background p-2">
            {variant === 'image' ? (
              <FileImage className="size-4 text-muted-foreground" />
            ) : (
              <FilePlayIcon className="size-4 text-muted-foreground" />
            )}
          </div>
          <div className="flex-1 min-w-0">
            <p className="truncate text-sm font-medium">{value.name}</p>
            <p className="text-xs text-muted-foreground">
              {(value.size / 1024 / 1024).toFixed(2)} MB
            </p>
          </div>
          <Button
            className="flex-none size-8"
            disabled={disabled}
            onClick={handleRemove}
            size="icon"
            type="button"
            variant="ghost"
          >
            <Trash2 className="size-4 text-muted-foreground hover:text-destructive" />
            <span className="sr-only">Usuń plik</span>
          </Button>
        </div>
      )}
    </div>
  );
};

export { FileInput };
