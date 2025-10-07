import { Button } from '@/components/ui/button';
import Typography from '@/components/ui/typography';
import { ArrowLeft } from 'lucide-react';

interface BackHeaderProps {
  title?: string;
  onBack: () => void;
}

function BackHeader({ title, onBack }: BackHeaderProps) {
  return (
    <div className="inline-flex items-center gap-3">
      <Button onClick={onBack} size={title ? 'icon' : 'default'} type="button" variant="ghost">
        <ArrowLeft className="size-4" />
        {!title && 'Powrót'}
      </Button>
      {title && <Typography variant="h3">{title}</Typography>}
    </div>
  );
}

export default BackHeader;
