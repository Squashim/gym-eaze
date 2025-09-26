import { Avatar, AvatarFallback, AvatarImage } from './avatar';
import Typography from './typography';

interface ReusableAvatarProps {
  username?: string;
  profilePicture?: string;
  className?: string;
  fallbackClassName?: string;
  variant?: 'small' | 'h4';
}

function ReusableAvatar({
  className,
  fallbackClassName,
  variant = 'small',
  username,
  profilePicture,
}: ReusableAvatarProps) {
  return (
    <Avatar className={className}>
      <AvatarImage
        alt={`zdjęcie profilowe użytkownika ${username || 'Nieznany'}`}
        src={profilePicture || undefined}
      />
      <AvatarFallback className={fallbackClassName}>
        <Typography className="font-medium" variant={variant}>
          {username?.slice(0, 2).toUpperCase()}
        </Typography>
      </AvatarFallback>
    </Avatar>
  );
}

export default ReusableAvatar;
