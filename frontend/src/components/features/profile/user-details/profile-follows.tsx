import { Button } from '@/components/ui/button';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import Typography from '@/components/ui/typography';
import type { Author } from '@/types/user';
import { ExternalLink, Info, Loader2 } from 'lucide-react';
import useGetFollowers from '@/hooks/query/user/use-get-followers';
import useGetFollowing from '@/hooks/query/user/use-get-following';
import { useMemo } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { cn } from '@/lib/utils';
import type { FollowsDialogType } from './profile-display';

interface ProfileFollowsProps {
  followersCount: number;
  followingCount: number;
  isOwnProfile: boolean;
  onClick: (type: FollowsDialogType) => void;
}

function ProfileFollows({
  followersCount,
  followingCount,
  isOwnProfile,
  onClick,
}: ProfileFollowsProps) {
  return (
    <div className="grid grid-cols-2 items-center gap-4 w-full">
      <Button
        className={cn(
          'flex flex-col h-auto px-2 py-1 items-start gap-0',
          !isOwnProfile && 'pointer-events-none',
        )}
        onClick={() => onClick('followers')}
        tabIndex={isOwnProfile ? 0 : -1}
        title={isOwnProfile ? 'Kliknij aby zobaczyć kto Cię obserwuje' : undefined}
        type="button"
        variant="outline"
      >
        <Typography className="font-semibold text-lg leading-normal">{followersCount}</Typography>
        <Typography className="leading-normal text-sm font-normal" variant="small">
          Obserwatorzy
        </Typography>
      </Button>

      <Button
        className={cn(
          'flex flex-col h-auto px-2 py-1 items-start gap-0',
          !isOwnProfile && 'pointer-events-none',
        )}
        onClick={() => onClick('following')}
        tabIndex={isOwnProfile ? 0 : -1}
        title={isOwnProfile ? 'Kliknij aby zobaczyć kogo obserwujesz' : undefined}
        type="button"
        variant="outline"
      >
        <Typography className="font-semibold text-lg leading-normal">{followingCount}</Typography>
        <Typography className="leading-normal text-sm font-normal" variant="small">
          Obserwowanie
        </Typography>
      </Button>
    </div>
  );
}

interface FollowUsersDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
  type: FollowsDialogType;
  count: number;
}
function FollowUsersDialog({ open, onOpenChange, onClose, type, count }: FollowUsersDialogProps) {
  const {
    data: followers,
    isLoading: isFollowersLoading,
    isError: isFollowersError,
  } = useGetFollowers({
    enabled: type === 'followers',
  });
  const {
    data: following,
    isLoading: isFollowingLoading,
    isError: isFollowingError,
  } = useGetFollowing({
    enabled: type === 'following',
  });

  const { isLoading, isError, users } = useMemo(() => {
    if (type === 'followers') {
      return {
        isLoading: isFollowersLoading,
        isError: isFollowersError,
        users: followers,
      };
    } else if (type === 'following') {
      return {
        isLoading: isFollowingLoading,
        isError: isFollowingError,
        users: following,
      };
    }
    return {
      isLoading: false,
      isError: false,
      users: [],
    };
  }, [
    type,
    followers,
    following,
    isFollowersLoading,
    isFollowingLoading,
    isFollowersError,
    isFollowingError,
  ]);

  if (!type) return null;

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden max-h-[80vh] overflow-hidden flex flex-col">
        <DialogHeader>
          <DialogTitle>
            {type === 'followers' ? `Twoi obserwujący (${count})` : `Obserwujesz (${count})`}
          </DialogTitle>
          <DialogDescription>
            {type === 'followers'
              ? 'Lista osób, które obserwują Twój profil i czerpią motywację z Twojej aktywności.'
              : 'Lista osób, które motywujesz, obserwując ich profile.'}
          </DialogDescription>
        </DialogHeader>

        <div className="flex-1 overflow-y-auto scrollbar">
          {isLoading ? (
            <div className="flex items-center justify-center h-48">
              <Loader2 className="animate-spin size-12" />
            </div>
          ) : isError ? (
            <div className="flex items-center justify-center h-48">
              <Typography className="text-center" variant="h4">
                Wystąpił nieoczekiwany błąd. Spróbuj odświeżyć stronę.
              </Typography>
            </div>
          ) : !users || users.length === 0 ? (
            <div className="flex items-center justify-center">
              <Alert>
                <Info />
                <AlertTitle className="line-clamp-2">
                  {type === 'followers'
                    ? 'Nie masz jeszcze żadnych obserwujących.'
                    : 'Nie obserwujesz jeszcze nikogo.'}
                </AlertTitle>
                <AlertDescription>
                  {type === 'followers'
                    ? 'Twoi obserwujący będą tutaj widoczni. Motywuj innych swoją aktywnością!'
                    : 'Obserwuj innych użytkowników, aby motywować ich do dalszej pracy!'}
                </AlertDescription>
              </Alert>
            </div>
          ) : (
            <div className="grid gap-2">
              {users.map((user) => (
                <UserItem key={user._id} user={user} />
              ))}
            </div>
          )}
        </div>

        <div className="flex justify-end">
          <Button onClick={onClose} type="button" variant="outline">
            Zamknij
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

interface UserItemProps {
  user: Author;
}

function UserItem({ user }: UserItemProps) {
  return (
    <div className="rounded-md p-3 shadow-md flex items-center gap-2 border bg-card text-card-foreground relative transition-all">
      <ReusableAvatar
        className="shadow-sm size-9"
        fallbackClassName="dark:bg-background"
        profilePicture={user.profileDetails?.profilePicture}
        username={user.username}
        variant="small"
      />

      <Typography className="text-sm font-semibold">{user.username}</Typography>

      <Button asChild className="ml-auto" size="sm" type="button" variant="outline">
        <a href={`/profile/${user.username}`} rel="noopener noreferrer" target="_blank">
          <ExternalLink />
          Zobacz profil
        </a>
      </Button>
    </div>
  );
}

export { ProfileFollows, FollowUsersDialog };
