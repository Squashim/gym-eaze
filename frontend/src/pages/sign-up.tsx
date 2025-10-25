import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import Typography from '@/components/ui/typography';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import image from '@/assets/images/register-page.webp';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { InfoIcon, Loader2, TriangleAlert } from 'lucide-react';
import { PasswordInput } from '@/components/ui/password-input';
import { signupSchema, type SignupSchema } from '@/schemas/signup-schema';
import GoogleLoginButton from '@/components/features/google-auth/google-login';
import { useSignup } from '@/hooks/auth/use-signup';
import { useGoogleLink } from '@/context/google-link';
import LinkGoogleAccountDialog from '@/components/features/google-auth/link-google-account-dialog';
import { signupDefaultValues } from '@/constants/defaults/forms';
import usePageTitle from '@/hooks/utils/use-page-title';

const Signup = () => {
  const form = useForm<SignupSchema>({
    resolver: zodResolver(signupSchema),
    defaultValues: signupDefaultValues,
  });
  const { signup } = useSignup(form.setError);
  const { googleLinkData } = useGoogleLink();
  usePageTitle('Załóż konto');

  const onSubmit = (data: SignupSchema) => {
    signup.mutate(data);
  };

  return (
    <>
      {!!googleLinkData && <LinkGoogleAccountDialog />}
      <div className="flex flex-col gap-6 max-w-lg md:max-w-5xl w-full mx-auto my-4">
        <Card className="overflow-hidden p-0">
          <CardContent className="grid p-0 md:grid-cols-2">
            <Form {...form}>
              <form className="p-6 md:p-8 order-1" onSubmit={form.handleSubmit(onSubmit)}>
                <div className="flex flex-col gap-6">
                  <header className="flex flex-col items-center text-center">
                    <Typography className="mb-1" variant="h1">
                      Zrób pierwszy krok
                    </Typography>
                    <Typography className="text-lg" variant="lead">
                      Załóż konto i dołącz do społeczności GymEaze
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
                    name="username"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="font-semibold">Nazwa użytkownika</FormLabel>
                        <FormControl>
                          <Input
                            {...field}
                            autoComplete="off"
                            autoFocus
                            maxLength={16}
                            placeholder="janek2000"
                            type="text"
                          />
                        </FormControl>
                        <FormMessage />
                        <FormDescription>
                          Nazwa użytkownika musi mieć od 6 do 16 znaków i może zawierać tylko litery
                          i cyfry.
                        </FormDescription>
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="font-semibold">Email</FormLabel>
                        <FormControl>
                          <Input
                            {...field}
                            autoComplete="email webauthn"
                            maxLength={320}
                            placeholder="jan@kowalski.pl"
                            type="email"
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
                            placeholder="••••••••••••"
                            showRequirements
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="confirmPassword"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="font-semibold">Potwierdź hasło</FormLabel>
                        <FormControl>
                          <PasswordInput
                            {...field}
                            autoComplete="off"
                            maxLength={32}
                            placeholder="••••••••••••"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <Button
                    className="w-full"
                    disabled={signup.isPending || !form.formState.isValid}
                    type="submit"
                  >
                    {signup.isPending ? (
                      <>
                        <Loader2 className="animate-spin" />
                        Rejestrowanie
                      </>
                    ) : (
                      'Zarejestruj się'
                    )}
                  </Button>

                  <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                    <span className="bg-card text-muted-foreground relative z-10 px-2">
                      Lub stwórz konto za pomocą
                    </span>
                  </div>

                  <GoogleLoginButton>Zarejestruj się przez Google</GoogleLoginButton>

                  <div className="text-center flex gap-1 items-center justify-center">
                    <Typography variant="small">Masz już konto?</Typography>
                    <Link
                      className="underline underline-offset-4 text-sm hover:text-accent-foreground transition-color"
                      to="/login"
                    >
                      Zaloguj się
                    </Link>
                  </div>
                </div>
              </form>
            </Form>
            <div className="relative hidden md:block md:min-h-[50rem]">
              <img
                alt="mężczyzna siedzący i wiążący sznurowadło butów"
                className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.5] aspect-[9/16]"
                height={800}
                loading="eager"
                src={image}
                width={447}
              />
              <Tooltip>
                <TooltipTrigger className="absolute left-4 bottom-4 text-muted-foreground">
                  <Link
                    aria-describedby="tooltip-description"
                    rel="noopener noreferrer"
                    target="_blank"
                    to="https://unsplash.com/photos/man-sitting-down-while-tying-shoe-lace-7RUvirkSMOY?utm_content=creditShareLink&utm_medium=referral&utm_source=unsplash"
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
          Tworząc konto zgadzasz się na <Link to="/">Warunki świadczenia usług</Link> i{' '}
          <Link to="/">Politykę prywatności</Link>.
        </div>
      </div>
    </>
  );
};

export default Signup;
