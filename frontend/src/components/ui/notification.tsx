import { BadgeAlert, BadgeInfo, BadgeX, Bell, Loader2, Trash2 } from 'lucide-react';
import { Button } from './button';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from './dropdown-menu';
import type { GetNotificationsApiResponse } from '@/types/notification';
import useGetNotifications from '@/hooks/query/notifications/use-get-notifications';
import { useCallback, useEffect, useMemo } from 'react';
import { parseNotificationsLabel, parseTimeAgo } from '@/lib/parsers';
import { useInView } from 'react-intersection-observer';
import ScrollLoader from './scroll-loader';
import { useNotification } from '@/hooks/query/notifications/use-notification';

const Notification = () => {
  const { data, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetNotifications();

  const { clearNotifications } = useNotification();

  const { ref, inView } = useInView({
    threshold: 1,
  });

  const notifications = useMemo(() => data?.pages.flatMap((page) => page.items) ?? [], [data]);
  const notificationsCount = useMemo(() => {
    return data?.pages[0]?.total ?? 0;
  }, [data]);

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage && !isLoading) {
      void fetchNextPage();
    }
  }, [inView, fetchNextPage, hasNextPage, isFetchingNextPage, isLoading]);

  const handleClearNotifications = useCallback(() => {
    clearNotifications.mutate();
  }, [clearNotifications]);

  return (
    <div className="fixed top-[14px] right-15 z-[60] sm:right-17 lg:right-30 [@media(min-width:1400px)]:right-[calc((100vw-1400px)/2+120px)]">
      <DropdownMenu>
        <NotificationTrigger
          isError={isError}
          isLoading={isLoading}
          notificationsCount={notificationsCount}
        />
        <DropdownMenuContent align="end" className="w-[280px] sm:w-sm">
          <NotificationHeader isError={isError} notificationsCount={notificationsCount} />

          <DropdownMenuSeparator />

          <NotificationContent
            hasNextPage={hasNextPage}
            isError={isError}
            isFetchingNextPage={isFetchingNextPage}
            notifications={notifications}
            notificationsCount={notificationsCount}
            ref={ref}
          />

          <DropdownMenuSeparator />

          <NotificationClear
            disabled={isError || notificationsCount === 0 || clearNotifications.isPending}
            isPending={clearNotifications.isPending}
            onClear={handleClearNotifications}
          />
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
};

interface NotificationTriggerProps {
  isError: boolean;
  isLoading: boolean;
  notificationsCount: number;
}

function NotificationTrigger({ isLoading, isError, notificationsCount }: NotificationTriggerProps) {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <DropdownMenuTrigger asChild>
          <Button
            aria-describedby="notification-tooltip-description"
            className="relative"
            disabled={isLoading}
            size="icon"
            variant="ghost"
          >
            {isLoading ? (
              <Loader2 className="animate-spin size-5" />
            ) : (
              <>
                <Bell className="size-5" />
                {!isError && notificationsCount > 0 && (
                  <Typography
                    className="absolute top-0 right-0 bg-secondary text-secondary-foreground size-4 p-0.5 rounded-sm font-medium text-xs flex items-center justify-center aspect-square"
                    variant="small"
                  >
                    {notificationsCount > 99 ? '99+' : notificationsCount}
                  </Typography>
                )}
              </>
            )}
          </Button>
        </DropdownMenuTrigger>
      </TooltipTrigger>
      <TooltipContent id="notification-tooltip-description">
        <Typography variant="small">Zobacz powiadomienia</Typography>
      </TooltipContent>
    </Tooltip>
  );
}

interface NotificationHeaderProps {
  isError: boolean;
  notificationsCount: number;
}

function NotificationHeader({ isError, notificationsCount }: NotificationHeaderProps) {
  return (
    <DropdownMenuLabel>
      {isError ? (
        <Typography className="font-semibold text-sm text-destructive" variant="p">
          Błąd podczas ładowania powiadomień
        </Typography>
      ) : (
        <Typography className="font-semibold text-sm text-foreground" variant="p">
          {notificationsCount > 0
            ? `Masz ${parseNotificationsLabel(notificationsCount)}`
            : 'Brak powiadomień'}
        </Typography>
      )}
    </DropdownMenuLabel>
  );
}

interface NotificationContentProps {
  notificationsCount: number;
  notifications: GetNotificationsApiResponse[];
  isFetchingNextPage: boolean;
  hasNextPage: boolean;
  ref: (node?: Element | null) => void;
  isError: boolean;
}

function NotificationContent({
  isError,
  notificationsCount,
  notifications,
  isFetchingNextPage,
  hasNextPage,
  ref,
}: NotificationContentProps) {
  if (isError) {
    return (
      <div className="min-h-[200px] flex items-center justify-center text-center w-[280px] sm:w-sm">
        <Typography className="text-base" variant="lead">
          Spróbuj ponownie później.
        </Typography>
      </div>
    );
  }

  if (notificationsCount === 0) {
    return (
      <div className="min-h-[200px] flex items-center justify-center text-center w-[280px] sm:w-sm">
        <Typography className="text-base" variant="lead">
          Tutaj pojawią się Twoje powiadomienia.
        </Typography>
      </div>
    );
  }

  return (
    <div className="max-h-[400px] overflow-y-auto scrollbar">
      {notifications.map((notification) => (
        <NotificationItem key={notification._id} notification={notification} />
      ))}
      <ScrollLoader
        hasNextPage={hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        itemLength={notificationsCount}
        itemType="notification"
        ref={ref}
      />
    </div>
  );
}

interface NotificationClearProps {
  disabled: boolean;
  isPending: boolean;
  onClear: () => void;
}

function NotificationClear({ disabled, isPending, onClear }: NotificationClearProps) {
  return (
    <DropdownMenuItem
      className="hover:bg-primary/90!
                  hover:text-primary-foreground! focus-visible:text-primary-foreground! focus-visible:bg-primary/90! dark:focus-visible:bg-primary/90! dark:focus-visible:text-primary-foreground! font-medium pl-3 h-10 transition-colors"
      disabled={disabled}
      onClick={(e) => {
        e.preventDefault();
        onClear();
      }}
    >
      {isPending ? (
        <Loader2 className="animate-spin text-inherit" />
      ) : (
        <Trash2 className="text-inherit" />
      )}
      {isPending ? 'Czyszczenie...' : 'Wyczyść powiadomienia'}
    </DropdownMenuItem>
  );
}

interface NotificationItemProps {
  notification: GetNotificationsApiResponse;
}

function NotificationItem({ notification }: NotificationItemProps) {
  const icon = {
    Informacja: <BadgeInfo className="size-6 text-secondary" />,
    Błąd: <BadgeX className="size-6 text-destructive" />,
    Ostrzeżenie: <BadgeAlert className="size-6 text-yellow-600 dark:text-yellow-400" />,
  }[notification.type];

  const isUserNotification =
    notification.from !== 'System' && notification.from !== 'Administrator';

  return (
    <DropdownMenuItem
      className="p-0 focus:bg-accent/70 border-b rounded-none last:border-0 cursor-default"
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          if (!isUserNotification) {
            e.preventDefault();
          } else {
            window.open(`/profile/${notification.from}`, '_blank', 'noreferrer');
          }
        }
      }}
      onSelect={(e) => {
        const target = e.target as HTMLElement;
        const isATag = target instanceof HTMLAnchorElement;
        if (!isUserNotification || !isATag) {
          e.preventDefault();
        }
      }}
    >
      <div className="flex gap-3 p-3 items-start w-full hover:bg-accent/50 transition-colors">
        <div className="flex-shrink-0 mt-0.5">{icon}</div>
        <div className="flex flex-col gap-1.5 min-w-0 flex-1">
          <Typography
            className="break-words whitespace-pre-wrap font-medium text-sm leading-relaxed"
            variant="small"
          >
            {notification.message}
          </Typography>

          <div className="flex items-center justify-between gap-2">
            {notification.from !== 'System' && notification.from !== 'Administrator' ? (
              <Button
                asChild
                className="p-0 text-xs h-auto font-medium text-muted-foreground"
                variant="link"
              >
                <a href={`/profile/${notification.from}`} rel="noreferrer" target="_blank">
                  Od: <strong>{notification.from}</strong>
                </a>
              </Button>
            ) : (
              <Typography className="text-xs font-medium text-muted-foreground" variant="muted">
                Od: <strong>{notification.from}</strong>
              </Typography>
            )}

            <Typography className="text-xs flex-shrink-0" variant="muted">
              {parseTimeAgo(notification.createdAt)}
            </Typography>
          </div>
        </div>
      </div>
    </DropdownMenuItem>
  );
}

export default Notification;
