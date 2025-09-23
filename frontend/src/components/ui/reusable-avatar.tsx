import { useUser } from '@/context/user';
import { Avatar, AvatarFallback, AvatarImage } from './avatar';
import Typography from './typography';
import { Loader2 } from 'lucide-react';

interface ReusableAvatarProps {
  className?: string;
  fallbackClassName?: string;
  variant?: 'small' | 'h4';
}

function ReusableAvatar({ className, fallbackClassName, variant = 'small' }: ReusableAvatarProps) {
  const { user, isLoading } = useUser();

  if (isLoading) {
    <Avatar className={className}>
      <AvatarFallback>
        <Typography className="font-medium" variant="h4">
          <Loader2 className="animate-spin size-4" />
        </Typography>
      </AvatarFallback>
    </Avatar>;
  }

  return (
    <Avatar className={className}>
      <AvatarImage
        alt={`zdjęcie profilowe użytkownika ${user?.username}`}
        src={user?.profileDetails.profilePicture || undefined}
      />
      <AvatarFallback className={fallbackClassName}>
        <Typography className="font-medium" variant={variant}>
          {user?.username.slice(0, 2).toUpperCase()}
        </Typography>
      </AvatarFallback>
    </Avatar>
  );
}

export default ReusableAvatar;
