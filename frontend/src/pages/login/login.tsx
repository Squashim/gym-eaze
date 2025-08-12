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
import GoogleLoginButton from '@/components/ui/google-login';

const Login = () => {
  const form = useForm<LoginSchema>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      login: '',
      password: '',
      rememberMe: false,
    },
  });
  const { login } = useAuth(form.setError);

  const onSubmit = (data: LoginSchema) => {
    login.mutate(data);
  };

  return (
    <div className="flex flex-col gap-6 max-w-lg md:max-w-5xl w-full mx-auto my-4">
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
                      <FormLabel
                        className="text-sm font-normal cursor-pointer"
                        htmlFor="remember-me"
                      >
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

                <GoogleLoginButton>Zaloguj się przez Google</GoogleLoginButton>

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
      <div className="text-muted-foreground *:[a]:hover:text-accent-foreground transition-color text-center text-xs text-balance *:[a]:underline *:[a]:underline-offset-4">
        Korzystając z serwisu zgadzasz się na <Link to="#">Warunki świadczenia usług</Link> i{' '}
        <Link to="#">Politykę prywatności</Link>.
      </div>
    </div>
  );
};

export default Login;
