import Logo from '@/components/ui/logo';
import { P } from '@/components/ui/typography';
import { Loader2 } from 'lucide-react';

const Loading = () => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-background/70 to-secondary/10">
      <div className="flex flex-col items-center animate-pulse gap-2">
        <Logo className="animate-bounce" size="lg" />
        <Loader2 className="w-12 h-12 text-foreground animate-spin" />
        <P>Rozgrzewamy mięśnie...</P>
      </div>
    </div>
  );
};

export default Loading;
