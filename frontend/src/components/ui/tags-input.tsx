import { Copy, Trash2, XCircle } from 'lucide-react';
import { Badge } from './badge';
import React, { useState } from 'react';
import { Textarea } from './textarea';
import { Button } from './button';
import { toast } from 'sonner';
import { cn } from '@/lib/utils';

interface TagsInputProps {
  value: string[];
  onChange: (value: string[]) => void;
  placeholder?: string;
  disabled?: boolean;
  maxTags?: number;
  id: string;
  tabIndex?: number;
  className?: string;
}

function TagsInput({
  value,
  onChange,
  placeholder,
  disabled,
  maxTags = 50,
  id,
  tabIndex = 0,
  className,
}: TagsInputProps) {
  const [input, setInput] = useState('');
  const inputRef = React.useRef<HTMLTextAreaElement>(null);

  const normalize = (s: string) =>
    s
      .toLowerCase()
      .trim()
      .replace(/\s+/g, '')
      .replace(/[^a-ząćęłńóśźż0-9]/g, '');

  const addTag = (raw: string) => {
    const t = normalize(raw);
    if (!t) return;
    if (value.some((v) => v.toLocaleLowerCase() === t.toLocaleLowerCase())) return;
    if (value.length >= maxTags) return;
    onChange([...value, t]);
  };

  const removeTag = (idx: number) => {
    const next = value.filter((_, i) => i !== idx);
    onChange(next);
  };

  const clearTags = () => {
    onChange([]);
    setInput('');
  };

  const handleBlur = () => {
    if (input) {
      addTag(input);
      setInput('');
    }
  };

  const handleKeyDown: React.KeyboardEventHandler<HTMLTextAreaElement> = (e) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      if (input) {
        addTag(input);
        setInput('');
      }
    } else if (e.key === 'Backspace' && input === '' && value.length > 0) {
      e.preventDefault();
      removeTag(value.length - 1);
    }
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(value.map((v) => v).join(','));
      toast.info(`Skopiowano ${value.length} hasztagów do schowka`);
    } catch {
      toast.error('Nie udało się skopiować hasztagów do schowka');
    }
  };

  const handlePaste: React.ClipboardEventHandler<HTMLTextAreaElement> = (e) => {
    const text = e.clipboardData.getData('text');
    if (text && (text.includes(',') || text.includes('\n'))) {
      e.preventDefault();
      const parts = text
        .split(/[\n,]/)
        .map((s) => normalize(s))
        .filter(Boolean);
      const next = [...value];
      for (const p of parts) {
        if (next.length >= maxTags) break;
        if (!next.some((v) => v.toLocaleLowerCase() === p.toLocaleLowerCase())) {
          next.push(p);
        }
      }
      onChange(next);
    }
  };

  return (
    <div
      aria-label="Pole formularza z hasztagami"
      className={cn(
        'dark:bg-input/30 border-input flex max-h-24 min-h-20 overflow-y-auto scrollbar w-full min-w-0 rounded-md border bg-transparent px-1 py-1 text-base shadow-xs duration-300 outline-none items-start flex-wrap gap-1 focus-within:border-ring focus-within:ring-[3px] focus-within:ring-ring/50 transition-all pr-12 relative',
        className,
      )}
      onClick={() => inputRef.current?.focus()}
      role="listbox"
    >
      {value.length > 6 && (
        <div className="absolute top-1 right-1 flex flex-col">
          <Button
            className="hover:shadow-sm hover:bg-white/50"
            onClick={handleCopy}
            size="icon"
            type="button"
            variant="ghost"
          >
            <Copy />
          </Button>
          <Button
            className="text-destructive hover:shadow-sm hover:text-destructive/80 hover:bg-white/50"
            onClick={clearTags}
            size="icon"
            type="button"
            variant="ghost"
          >
            <Trash2 />
          </Button>
        </div>
      )}

      {value.map((tag, index) => (
        <Badge
          className="m-1 transition-all duration-300 ease-in-out border-foreground/10 text-foreground bg-card hover:bg-card/80 hover:-translate-y-1 hover:scale-110"
          key={tag}
        >
          <span>{`#${tag}`}</span>
          <div
            aria-label={`Usuń #${tag}`}
            className="ml-2 h-4 w-4 cursor-pointer dark:hover:bg-white/20 hover:bg-ring/50 rounded-sm -m-0.5 focus:outline-none focus:ring-1 dark:focus:ring-white/50 focus:ring-ring flex items-center justify-center"
            onClick={(event) => {
              event.stopPropagation();
              removeTag(index);
            }}
            onKeyDown={(event) => {
              if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                event.stopPropagation();
                removeTag(index);
              }
            }}
            role="button"
            tabIndex={0}
          >
            <XCircle className="w-4 h-4" />
          </div>
        </Badge>
      ))}

      <Textarea
        aria-label="Dodaj hasztag"
        className="border-0 !bg-transparent m-0 !shadow-none focus-visible:ring-0 flex-1 h-auto focus-visible:outline-none resize-none min-w-42"
        data-type="hashtag-input"
        disabled={disabled || value.length >= maxTags}
        id={id}
        maxLength={25}
        onBlur={handleBlur}
        onChange={(e) => setInput(e.target.value)}
        onKeyDown={handleKeyDown}
        onPaste={handlePaste}
        placeholder={value.length > 0 ? 'Dodaj kolejny hasztag...' : placeholder}
        ref={inputRef}
        tabIndex={tabIndex}
        value={input}
      />
    </div>
  );
}

export { TagsInput };
