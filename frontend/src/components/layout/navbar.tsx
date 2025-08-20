import Logo from '../ui/logo';
import { useUser } from '@/context/user';
import { useLogout } from '@/hooks/auth/use-logout';
import { Button } from '../ui/button';
import ThemeToggle from '../ui/theme-toggle';
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from '@/components/ui/navigation-menu';
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { authorizedLinks, links } from '@/constants/navbar';
import type { MenuItem } from '@/types/navbar';
import { Loader2, LucideLogIn, LucideLogOut, Menu, X } from 'lucide-react';
import Typography from '../ui/typography';
import { useMediaQuery } from '@/hooks/use-media-query';
import { DropdownMenu } from '@radix-ui/react-dropdown-menu';
import {
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Separator } from '../ui/separator';
import Notification from '../ui/notification';

type NavbarProps = {
  isLoggedIn: boolean;
  isAdmin: boolean;
};

const Navbar = ({ isLoggedIn, isAdmin }: NavbarProps) => {
  const { logout } = useLogout();
  const { user } = useUser();

  const handleLogout = () => {
    logout.mutate();
  };

  const renderMenuItem = (item: MenuItem) => {
    if (item.allow === 'logged-in' && !isLoggedIn) {
      return null;
    }

    if (item.allow === 'not-logged-in' && isLoggedIn) {
      return null;
    }

    if (item.allow === 'logged-in' && item.role === 'admin' && !isAdmin) {
      return null;
    }

    if (item.items) {
      return (
        <NavigationMenuItem key={item.title}>
          <NavigationMenuTrigger
            className="data-[active=true]:bg-accent/50 data-[active=true]:text-accent-foreground data-[active=true]:hover:bg-accent"
            data-active={item.items.some((subItem) => window.location.pathname === subItem.url)}
          >
            {item.title}
          </NavigationMenuTrigger>
          <NavigationMenuContent className="bg-popover text-popover-foreground ">
            {item.items.map((subItem) => {
              return (
                <NavigationMenuLink asChild className="w-80" key={subItem.title}>
                  <SubMenuLink
                    disabled={subItem.allow === 'logged-in' && !isLoggedIn}
                    item={subItem}
                  />
                </NavigationMenuLink>
              );
            })}
          </NavigationMenuContent>
        </NavigationMenuItem>
      );
    }

    return (
      <NavigationMenuItem key={item.title}>
        <NavigationMenuLink
          className="bg-background inline-flex w-max items-center justify-center rounded-md px-4 font-medium"
          data-active={window.location.pathname === item.url}
          href={item.url}
        >
          {item.title}
        </NavigationMenuLink>
      </NavigationMenuItem>
    );
  };

  const renderMobileMenuItem = (item: MenuItem) => {
    if (item.items) {
      return (
        <AccordionItem className="border-b-0" key={item.title} value={item.title}>
          <AccordionTrigger
            className="text-md p-2 rounded-md font-medium cursor-pointer hover:bg-accent data-[active=true]:bg-accent/50 data-[active=true]:text-accent-foreground data-[active=true]:hover:bg-accent  outline-none focus-visible:ring-[3px] focus-visible:outline-1 h-10 focus-visible:ring-ring/50 hover:no-underline data-[state=open]:hover:bg-accent data-[state=open]:text-accent-foreground data-[state=open]:bg-accent/50"
            data-active={item.items.some((subItem) => window.location.pathname === subItem.url)}
          >
            {item.title}
          </AccordionTrigger>
          <AccordionContent className="mt-2 px-1">
            {item.items.map((subItem) => (
              <SubMenuLink
                disabled={subItem.allow === 'logged-in' && !isLoggedIn}
                item={subItem}
                key={subItem.title}
              />
            ))}
          </AccordionContent>
        </AccordionItem>
      );
    }

    return (
      <a
        className="flex items-center text-md font-medium rounded-md p-2 leading-none transition-all hover:bg-accent data-[active=true]:bg-accent/50 data-[active=true]:text-accent-foreground data-[active=true]:hover:bg-accent outline-none focus-visible:ring-[3px] focus-visible:outline-1 h-10 focus-visible:ring-ring/50"
        data-active={window.location.pathname === item.url}
        href={item.url}
        key={item.title}
      >
        {item.title}
      </a>
    );
  };

  const renderMobileNavbar = () => {
    return (
      <div className="block lg:hidden container">
        <div className="flex items-center justify-between">
          <Logo redirect />
          <div className="flex items-center gap-4">
            <Sheet>
              <SheetTrigger asChild>
                <Button size="icon" variant="outline">
                  <Menu className="size-5" />
                </Button>
              </SheetTrigger>
              <SheetContent className="overflow-y-auto scrollbar [&>button:first-of-type]:hidden">
                <SheetHeader>
                  <SheetTitle>
                    <Logo redirect />
                  </SheetTitle>
                  <SheetClose asChild>
                    <Button className="absolute right-4 top-7" size="icon" variant="outline">
                      <X className="size-5" />
                    </Button>
                  </SheetClose>
                  <SheetDescription className="text-accent-foreground/80">
                    Wybierz jedną z dostępnych opcji aby przejść do odpowiedniej strony.
                  </SheetDescription>
                </SheetHeader>
                <div className="flex flex-col gap-8 p-4">
                  <Accordion className="flex w-full flex-col gap-4" collapsible type="single">
                    {links.map((item) => renderMobileMenuItem(item))}
                  </Accordion>
                  {isLoggedIn && <Separator className="h-2 bg-accent" />}
                  <div className="flex flex-col gap-4">
                    {!isLoggedIn && <GuestButtons />}
                    {isLoggedIn && user && (
                      <div className="flex w-full flex-col gap-4">
                        <div className="flex items-center gap-4">
                          <Avatar className="size-12">
                            <AvatarImage
                              alt={`zdjęcie profilowe użytkownika ${user.username}`}
                              src={user.profileDetails.profilePicture || undefined}
                            />
                            <AvatarFallback>
                              <Typography className="font-medium" variant="h4">
                                {user.username.slice(0, 2).toUpperCase()}
                              </Typography>
                            </AvatarFallback>
                          </Avatar>
                          <div className="flex flex-col">
                            <Typography className="font-semibold text-base" variant="p">
                              {user.username}
                            </Typography>
                            <Typography className="text-xs text-muted-foreground" variant="small">
                              {isAdmin ? 'Administrator' : 'Użytkownik'}
                            </Typography>
                          </div>
                        </div>
                        {authorizedLinks.map((item) => {
                          const urlWithUsername = item.url.replace(':username', user.username);
                          return renderMobileMenuItem({ ...item, url: urlWithUsername });
                        })}
                        <Button disabled={logout.isPending} onClick={handleLogout}>
                          {logout.isPending ? (
                            <>
                              <Loader2 className="animate-spin" />
                              Wylogowywanie
                            </>
                          ) : (
                            <>
                              <LucideLogOut className="text-inherit" />
                              Wyloguj się
                            </>
                          )}
                        </Button>
                      </div>
                    )}
                    <ThemeToggle />
                  </div>
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </div>
    );
  };

  return (
    <header className="sticky top-0 z-50 w-full bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 rounded-b-lg shadow-lg h-navbar">
      <nav className="hidden lg:grid lg:grid-cols-3 items-center container">
        <div className="flex justify-start ">
          <Logo redirect />
        </div>
        <div className="flex items-center justify-center space-x-2">
          <NavigationMenu>
            <NavigationMenuList>{links.map((item) => renderMenuItem(item))}</NavigationMenuList>
          </NavigationMenu>
        </div>
        <div className="flex items-center gap-2 justify-end">
          {!isLoggedIn && <GuestButtons />}
          {isLoggedIn && user && (
            <DropdownMenu modal={false}>
              <DropdownMenuTrigger asChild tabIndex={0}>
                <Avatar className="cursor-pointer">
                  <AvatarImage
                    alt={`zdjęcie profilowe użytkownika ${user.username}`}
                    src={user.profileDetails.profilePicture || undefined}
                  />
                  <AvatarFallback className="dark:bg-background">
                    <Typography variant="small">
                      {user.username.slice(0, 2).toUpperCase()}
                    </Typography>
                  </AvatarFallback>
                </Avatar>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="hidden lg:flex flex-col">
                <DropdownMenuLabel>
                  <div className="flex flex-col">
                    <Typography className="font-semibold text-base text-foreground" variant="p">
                      {user.username}
                    </Typography>
                    <Typography className="text-xs text-muted-foreground" variant="small">
                      {isAdmin ? 'Administrator' : 'Użytkownik'}
                    </Typography>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                {authorizedLinks.map((item) => {
                  const urlWithUsername = item.url.replace(':username', user.username);

                  return (
                    <DropdownMenuItem asChild key={item.title}>
                      <a
                        className="h-10 flex items-center gap-1 data-[active=true]:bg-muted/50 hover:bg-muted! focus-visible:bg-muted!"
                        data-active={window.location.pathname === urlWithUsername}
                        href={urlWithUsername}
                      >
                        {item.icon && <item.icon className="size-5" />}
                        {item.title}
                      </a>
                    </DropdownMenuItem>
                  );
                })}
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  className="hover:bg-primary/90!
                  hover:text-primary-foreground! focus-visible:text-primary-foreground! focus-visible:bg-primary/90! dark:focus-visible:bg-primary/90! dark:focus-visible:text-primary-foreground! font-medium pl-3 h-10 transition-colors"
                  disabled={logout.isPending}
                  onClick={(e) => {
                    e.preventDefault();
                    handleLogout();
                  }}
                >
                  {logout.isPending ? (
                    <>
                      <Loader2 className="animate-spin" />
                      Wylogowywanie
                    </>
                  ) : (
                    <>
                      <LucideLogOut className="text-inherit" />
                      Wyloguj się
                    </>
                  )}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
          <ThemeToggle />
        </div>
      </nav>

      {isLoggedIn && user && <Notification notifications={user.notifications} />}

      {renderMobileNavbar()}
    </header>
  );
};

const GuestButtons = () => {
  const isSmallLogin = useMediaQuery('(width >= 1024px) and (width <= 1280px)');

  return (
    <>
      {isSmallLogin ? (
        <Button asChild size="icon" variant="outline">
          <a href="/login">
            <LucideLogIn />
          </a>
        </Button>
      ) : (
        <Button asChild variant="outline">
          <a href="/login">Zaloguj się</a>
        </Button>
      )}
      <Button asChild>
        <a href="/signup">Załóż konto</a>
      </Button>
    </>
  );
};

const SubMenuLink = ({ item, disabled = false }: { item: MenuItem; disabled: boolean }) => {
  return (
    <a
      aria-disabled={disabled}
      className="flex w-full lg:w-80 items-center hover:bg-muted hover:text-accent-foreground select-none gap-4 rounded-md p-3 leading-none no-underline transition-colors data-[active=true]:bg-accent/50 data-[active=true]:text-accent-foreground lg:data-[active=true]:bg-muted/50 data-[active=true]:hover:bg-accent lg:data-[active=true]:hover:bg-muted data-[disabled=true]:pointer-events-none data-[disabled=true]:opacity-50"
      data-active={window.location.pathname === item.url}
      data-disabled={disabled}
      href={item.url}
      tabIndex={disabled ? -1 : 0}
    >
      {item.icon && <item.icon className="size-5 shrink-0" />}
      <div className="flex flex-col gap-1">
        <Typography variant="small">{item.title}</Typography>
        {item.description && (
          <Typography className="leading-snug" variant="muted">
            {item.description}
          </Typography>
        )}
      </div>
    </a>
  );
};

export default Navbar;
