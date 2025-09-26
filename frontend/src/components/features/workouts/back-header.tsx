import { Button } from '@/components/ui/button';
import Typography from '@/components/ui/typography';
import { ArrowLeft } from 'lucide-react';

interface BackHeaderProps {
  title: string;
  onBack: () => void;
}

function BackHeader({ title, onBack }: BackHeaderProps) {
  return (
    <div className="inline-flex items-center  gap-3">
      <Button onClick={onBack} size="icon" type="button" variant="ghost">
        <ArrowLeft className="size-4" />
      </Button>
      <Typography variant="h3">{title}</Typography>
    </div>
  );
}

export default BackHeader;
