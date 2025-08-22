import Logo from '@/components/ui/logo';
import Typography from '@/components/ui/typography';
import { Loader2 } from 'lucide-react';

const Loading = () => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-background/70 to-secondary/10">
      <div className="flex flex-col items-center animate-pulse gap-2 relative mt-24">
        <Logo className="animate-bounce absolute bottom-16" size="lg" />
        <Loader2
          className="w-64 h-64 text-foreground animate-spin absolute bottom-8"
          strokeWidth={0.5}
        />
        <Typography variant="lead">Rozgrzewamy mięśnie...</Typography>
      </div>
    </div>
  );
};

export default Loading;
