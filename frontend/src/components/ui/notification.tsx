import { BadgeAlert, BadgeInfo, BadgeX, Bell, Trash2 } from 'lucide-react';
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
import type { Notification as NotificationType } from '@/types/notification';

type NotificationProps = {
  notifications: NotificationType[] | undefined;
};

const Notification = ({ notifications }: NotificationProps) => {
  const number = notifications?.length || 0;

  const renderNotificaton = (notification: NotificationType) => {
    const icon = {
      Informacja: <BadgeInfo className="size-6 text-secondary" />,
      Błąd: <BadgeX className="size-6 text-destructive" />,
      Ostrzeżenie: <BadgeAlert className="size-6 text-yellow-600 dark:text-yellow-400" />,
    }[notification.type];

    return (
      <DropdownMenuItem asChild key={notification._id}>
        <div className="flex gap-2 px-2 py-1.5 items-center w-90">
          {icon}
          <div className="flex flex-col gap-1">
            <Typography variant="small">{notification.message}</Typography>
            <Typography className="text-xs text-muted-foreground" variant="small">
              {new Date(notification.createdAt).toLocaleString()}
            </Typography>
          </div>
        </div>
      </DropdownMenuItem>
    );
  };

  return (
    <div className="fixed top-[14px] right-15 z-[60] sm:right-17 lg:right-30 [@media(min-width:1400px)]:right-[calc((100vw-1400px)/2+120px)]">
      <DropdownMenu>
        <Tooltip>
          <TooltipTrigger asChild>
            <DropdownMenuTrigger asChild>
              <Button
                aria-describedby="notification-tooltip-description"
                className="relative"
                size="icon"
                variant="ghost"
              >
                <Bell className="size-5" />
                {number > 0 && (
                  <Typography
                    className="absolute top-0 right-0 bg-secondary text-secondary-foreground size-4 p-0.5 rounded-sm font-medium text-xs flex items-center justify-center aspect-square"
                    variant="small"
                  >
                    {number}
                  </Typography>
                )}
              </Button>
            </DropdownMenuTrigger>
          </TooltipTrigger>
          <TooltipContent id="notification-tooltip-description">
            <Typography variant="small">Zobacz powiadomienia</Typography>
          </TooltipContent>
        </Tooltip>
        <DropdownMenuContent align="end">
          <DropdownMenuLabel>
            <Typography className="font-semibold text-sm text-foreground" variant="p">
              {number > 0 ? `Masz ${number} nowe powiadomienia!` : 'Brak powiadomień'}
            </Typography>
          </DropdownMenuLabel>

          {number > 0 && (
            <>
              <DropdownMenuSeparator />
              {notifications!.map((notification) => renderNotificaton(notification))}
              <DropdownMenuSeparator />
              <DropdownMenuItem
                className="text-destructive hover:bg-destructive!
                  hover:text-primary-foreground! focus-visible:text-primary-foreground! focus-visible:bg-destructive! dark:focus-visible:bg-destructive! dark:focus-visible:text-primary-foreground! font-medium h-10 transition-colors"
                disabled={number === 0}
                onClick={(e) => {
                  e.preventDefault();
                  // TODO: Implement clear notifications logic here
                }}
              >
                <Trash2 className="text-inherit" />
                Wyczyść powiadomenia
              </DropdownMenuItem>
            </>
          )}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
};

export default Notification;
