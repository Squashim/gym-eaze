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
import Typography from '@/components/ui/typography';
import { loginSchema, type LoginSchema } from '@/schemas/login-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import image from '@/assets/images/login-page.webp';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { InfoIcon, Loader2, TriangleAlert } from 'lucide-react';
import { PasswordInput } from '@/components/ui/password-input';
import { Checkbox } from '@/components/ui/checkbox';
import GoogleLoginButton from '@/components/features/google-auth/google-login';
import { useLogin } from '@/hooks/auth/use-login';
import { useGoogleLink } from '@/context/google-link';
import LinkGoogleAccountDialog from '@/components/features/google-auth/link-google-account-dialog';
import { loginDefaultValues } from '@/constants/defaults/forms';
import usePageTitle from '@/hooks/utils/use-page-title';

const Login = () => {
  const form = useForm<LoginSchema>({
    resolver: zodResolver(loginSchema),
    defaultValues: loginDefaultValues,
  });
  const { login } = useLogin(form.setError);
  const { googleLinkData } = useGoogleLink();
  usePageTitle('Logowanie');

  const onSubmit = (data: LoginSchema) => {
    login.mutate(data);
  };

  return (
    <>
      {!!googleLinkData && <LinkGoogleAccountDialog />}

      <div className="flex flex-col gap-6 max-w-lg md:max-w-5xl w-full mx-auto my-4">
        <Card className="overflow-hidden p-0 my-4 max-w-lg md:max-w-4xl w-full mx-auto">
          <CardContent className="grid p-0 md:grid-cols-2">
            <Form {...form}>
              <form className="p-6 md:p-8" onSubmit={form.handleSubmit(onSubmit)}>
                <div className="flex flex-col gap-6">
                  <header className="flex flex-col items-center text-center">
                    <Typography className="mb-1" variant="h1">
                      Witaj ponownie
                    </Typography>
                    <Typography className="text-lg" variant="lead">
                      Zaloguj się do swojego konta GymEaze
                    </Typography>
                  </header>
                  {form.formState.errors.root && (
                    <div className="w-full bg-destructive/10 flex items-center gap-2 border border-destructive rounded-md  text-left p-2">
                      <TriangleAlert className="text-destructive" size={18} />
                      <Typography className="text-destructive" variant="small">
                        {form.formState.errors.root.message}
                      </Typography>
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
                    <Typography variant="small">Nie masz konta?</Typography>
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
                  <Typography variant="small">Informacje o autorze zdjęcia</Typography>
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
    </>
  );
};

export default Login;
