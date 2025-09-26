import { Info } from 'lucide-react';
import Typography from './typography';
import type { PropsWithChildren } from 'react';

function Message({ children }: PropsWithChildren) {
  return (
    <div className="rounded-md p-3 shadow-md flex items-center gap-2 border">
      <Info className="size-4" />
      <Typography className="text-sm">{children}</Typography>
    </div>
  );
}

export default Message;
