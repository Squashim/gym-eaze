import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { H1, Lead, Small } from '@/components/ui/typography';
import { loginSchema, type LoginSchema } from '@/schemas/login-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import image from '@/assets/images/login-page.webp';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { InfoIcon } from 'lucide-react';
import { PasswordInput } from '@/components/ui/password-input';
import { Checkbox } from '@/components/ui/checkbox';

const Login = () => {
  const form = useForm<LoginSchema>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      login: '',
      password: '',
      rememberMe: false,
    },
  });

  const onSubmit = (data: LoginSchema) => {
    console.log('Form submitted:', data);
  };

  return (
    <Card className="overflow-hidden p-0 my-4 max-w-lg md:max-w-4xl w-full mx-auto">
      <CardContent className="grid p-0 md:grid-cols-2">
        <Form {...form}>
          <form className="p-6 md:p-8" onSubmit={form.handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6">
              <header className="flex flex-col items-center text-center">
                <H1 className="mb-0.5">Witaj ponownie</H1>
                <Lead className="text-base">Zaloguj się do swojego konta GymEaze</Lead>
              </header>
              <FormField
                control={form.control}
                name="login"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="font-semibold">Email lub nazwa użytkownika</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        autoComplete="email webauthn"
                        autoFocus
                        placeholder="jan@kowalski.pl"
                        type="text"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="font-semibold">Hasło</FormLabel>
                    <FormControl>
                      <PasswordInput
                        {...field}
                        autoComplete="current-password webauthn"
                        placeholder="••••••••••••"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="rememberMe"
                render={({ field }) => (
                  <FormItem className="flex items-center gap-2">
                    <FormControl>
                      <Checkbox
                        checked={field.value}
                        id="remember-me"
                        name="rememberMe"
                        onCheckedChange={(checked) => field.onChange(!!checked)}
                      />
                    </FormControl>
                    <FormLabel className="text-sm font-normal" htmlFor="remember-me">
                      Zapamiętaj mnie
                    </FormLabel>
                  </FormItem>
                )}
              />

              <Button className="w-full" type="submit">
                Zaloguj się
              </Button>

              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-card text-muted-foreground relative z-10 px-2">
                  Lub zaloguj się za pomocą
                </span>
              </div>

              <Button className="w-full" type="button" variant="outline">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
                    fill="currentColor"
                  />
                </svg>
                Zaloguj się przez Google
                <span className="sr-only">Logowanie kontem Google</span>
              </Button>

              <div className="text-center flex gap-1 items-center justify-center">
                <Small>Nie masz konta?</Small>
                <Link
                  className="underline underline-offset-4 text-sm hover:text-accent-foreground transition-color"
                  to="/signup"
                >
                  Zarejestruj się
                </Link>
              </div>
            </div>
          </form>
        </Form>
        <div className="relative hidden md:block ">
          <img
            alt="mężczyzna ćwiczy z hantlami na siłowni"
            className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.5]  aspect-[9/16]"
            height={500}
            loading="lazy"
            src={image}
            width={357}
          />
          <Tooltip>
            <TooltipTrigger className="absolute right-4 bottom-4 text-muted-foreground">
              <Link
                rel="noopener noreferrer"
                target="_blank"
                to="https://unsplash.com/photos/a-man-is-doing-exercises-with-dumbs-in-a-gym-t9bG_FOXqMA"
              >
                <InfoIcon size={20} />
              </Link>
            </TooltipTrigger>
            <TooltipContent>
              <Small>Informacje o autorze zdjęcia</Small>
            </TooltipContent>
          </Tooltip>
        </div>
      </CardContent>
    </Card>
  );
};

export default Login;
