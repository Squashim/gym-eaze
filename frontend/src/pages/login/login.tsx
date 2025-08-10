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
import { InfoIcon, Loader2, TriangleAlert } from 'lucide-react';
import { PasswordInput } from '@/components/ui/password-input';
import { Checkbox } from '@/components/ui/checkbox';
import { useAuth } from '@/hooks/useAuth';
import { useGoogleLogin } from '@react-oauth/google';
import { toast } from 'sonner';

const Login = () => {
  const form = useForm<LoginSchema>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      login: 'monikaGym',
      password: 'TestPasswordddd123!',
      rememberMe: false,
    },
  });
  const { login } = useAuth(form.setError);
  const googleLogin = useGoogleLogin({
    onSuccess: (credentialResponse) => {
      console.log(credentialResponse);
    },
    onError: () => {
      toast.error('Wystąpił błąd podczas logowania przez Google. Spróbuj ponownie później.');
    },
  });

  const onSubmit = (data: LoginSchema) => {
    login.mutate(data);
  };

  return (
    <Card className="overflow-hidden p-0 my-4 max-w-lg md:max-w-4xl w-full mx-auto">
      <CardContent className="grid p-0 md:grid-cols-2">
        <Form {...form}>
          <form className="p-6 md:p-8" onSubmit={form.handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6">
              <header className="flex flex-col items-center text-center">
                <H1 className="mb-1">Witaj ponownie</H1>
                <Lead className="text-lg">Zaloguj się do swojego konta GymEaze</Lead>
              </header>
              {form.formState.errors.root && (
                <div className="w-full bg-destructive/10 flex items-center gap-2 border border-destructive rounded-md  text-left p-2">
                  <TriangleAlert className="text-destructive" size={18} />
                  <Small className="text-destructive">{form.formState.errors.root.message}</Small>
                </div>
              )}
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
                        maxLength={320}
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
                        maxLength={32}
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
                        checked={!!field.value}
                        id="remember-me"
                        name="rememberMe"
                        onCheckedChange={(checked) => field.onChange(!!checked)}
                      />
                    </FormControl>
                    <FormLabel className="text-sm font-normal cursor-pointer" htmlFor="remember-me">
                      Zapamiętaj mnie
                    </FormLabel>
                  </FormItem>
                )}
              />

              <Button
                className="w-full"
                disabled={login.isPending || !form.formState.isValid}
                type="submit"
              >
                {login.isPending ? (
                  <>
                    <Loader2 className="animate-spin" />
                    Logowanie
                  </>
                ) : (
                  'Zaloguj się'
                )}
              </Button>

              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-card text-muted-foreground relative z-10 px-2">
                  Lub zaloguj się za pomocą
                </span>
              </div>

              <Button
                className="w-full"
                onClick={() => googleLogin()}
                type="button"
                variant="outline"
              >
                <svg
                  version="1.1"
                  viewBox="0 0 48 48"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlnsXlink="http://www.w3.org/1999/xlink"
                >
                  <path
                    d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
                    fill="#EA4335"
                  ></path>
                  <path
                    d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
                    fill="#4285F4"
                  ></path>
                  <path
                    d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
                    fill="#FBBC05"
                  ></path>
                  <path
                    d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
                    fill="#34A853"
                  ></path>
                  <path d="M0 0h48v48H0z" fill="none"></path>
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
        <div className="relative hidden md:block md:min-h-[40rem]">
          <img
            alt="mężczyzna ćwiczy z hantlami na siłowni"
            className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.5] aspect-[9/16]"
            height={600}
            loading="eager"
            src={image}
            width={447}
          />
          <Tooltip>
            <TooltipTrigger className="absolute right-4 bottom-4 text-muted-foreground">
              <Link
                aria-describedby="tooltip-description"
                rel="noopener noreferrer"
                target="_blank"
                to="https://unsplash.com/photos/a-man-is-doing-exercises-with-dumbs-in-a-gym-t9bG_FOXqMA"
              >
                <span className="sr-only">Informacje o autorze zdjęcia</span>
                <InfoIcon aria-hidden="true" size={20} />
              </Link>
            </TooltipTrigger>
            <TooltipContent id="tooltip-description">
              <Small>Informacje o autorze zdjęcia</Small>
            </TooltipContent>
          </Tooltip>
        </div>
      </CardContent>
    </Card>
  );
};

export default Login;
