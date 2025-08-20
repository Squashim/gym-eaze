import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { useState } from 'react';
import { useGoogleLink } from '@/context/google-link';
import { toast } from 'sonner';
import { useNavigate } from 'react-router-dom';
import {
  linkGoogleAccountSchema,
  type LinkGoogleAccountSchema,
} from '@/schemas/link-google-account-schema';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import type { LinkGoogleAccountPayload } from '@/types/auth';
import { useLinkGoogleAccount } from '@/hooks/auth/use-link-google-account';
import { Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { PasswordInput } from '@/components/ui/password-input';

const LinkGoogleAccountDialog = () => {
  const navigate = useNavigate();
  const { linkGoogleAccount } = useLinkGoogleAccount();
  const { googleLinkData, setGoogleLinkData } = useGoogleLink();
  const [step, setStep] = useState(1);

  const form = useForm<LinkGoogleAccountSchema>({
    resolver: zodResolver(linkGoogleAccountSchema),
    defaultValues: {
      email: googleLinkData?.email || '',
      password: '',
      token: googleLinkData?.token || '',
    },
  });

  const handleGoogleLinkCancel = () => {
    setGoogleLinkData(undefined);
    toast.info(
      'Połączenie konta Google anulowane. Możesz zalogować się poprzez formularz logowania.',
    );
    void navigate('/login', { replace: true });
  };

  const onSubmit = (data: LinkGoogleAccountPayload) => {
    linkGoogleAccount.mutate(data);
  };

  const handleNextStep = () => {
    setStep(2);
  };

  return (
    <Dialog defaultOpen={true} onOpenChange={(isOpen) => !isOpen && handleGoogleLinkCancel()}>
      <DialogContent className="[&>button]:hidden" onInteractOutside={(e) => e.preventDefault()}>
        <DialogHeader>
          {step === 1 && (
            <>
              <DialogTitle>Połącz swoje konto Google z GymEaze</DialogTitle>
              <DialogDescription>
                Wykryliśmy, że Twój adres e-mail jest już powiązany z innym kontem GymEaze. Czy
                chcesz połączyć swoje konta, aby móc logować się jeszcze wygodniej? Po połączeniu
                będziesz mógł używać zarówno zwykłego logowania, jak i szybkiego logowania przez
                Google.
              </DialogDescription>
            </>
          )}

          {step === 2 && googleLinkData && (
            <>
              <DialogTitle>Wprowadź hasło, aby połączyć konto Google</DialogTitle>
              <Form {...form}>
                <form
                  className="py-6"
                  id="link-google-account-form"
                  onSubmit={form.handleSubmit(onSubmit)}
                >
                  <div className="flex flex-col gap-6">
                    <FormField
                      control={form.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="font-semibold">Twój adres e-mail</FormLabel>
                          <FormControl>
                            <Input {...field} disabled readOnly type="email" />
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
                          <FormLabel className="font-semibold">Potwierdź hasło</FormLabel>
                          <FormControl>
                            <PasswordInput
                              {...field}
                              autoComplete="off"
                              autoFocus
                              maxLength={32}
                              placeholder="••••••••••••"
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                </form>
              </Form>
            </>
          )}
        </DialogHeader>
        <DialogFooter>
          {step === 1 && (
            <>
              <DialogClose asChild>
                <Button type="button" variant="outline">
                  Pomiń
                </Button>
              </DialogClose>
              <Button
                autoFocus
                className="focus:outline-none focus:ring-[3px] focus:ring-primary/20 dark:focus:ring-primary/40"
                onClick={handleNextStep}
                type="button"
              >
                Tak, połącz konta
              </Button>
            </>
          )}

          {step === 2 && (
            <>
              <DialogClose asChild>
                <Button type="button" variant="outline">
                  Anuluj
                </Button>
              </DialogClose>

              <Button
                disabled={linkGoogleAccount.isPending || !form.formState.isValid}
                form="link-google-account-form"
                type="submit"
              >
                {linkGoogleAccount.isPending ? (
                  <>
                    <Loader2 className="animate-spin" />
                    Autoryzowanie
                  </>
                ) : (
                  ' Autoryzuj połączenie'
                )}
              </Button>
            </>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default LinkGoogleAccountDialog;
