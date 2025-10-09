import { useUserProfile } from '@/hooks/query/user/use-user-profile';
import { loginDetailsSchema, type LoginDetailsSchema } from '@/schemas/user-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useCallback, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { RemoveAccountCard, TabButtons, TabContent } from './tabs-ui';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { useUser } from '@/context/user';
import { Input } from '@/components/ui/input';
import { PasswordInput } from '@/components/ui/password-input';
import { TriangleAlert } from 'lucide-react';
import Typography from '@/components/ui/typography';

function LoginDetailsTab() {
  const form = useForm<LoginDetailsSchema>({
    resolver: zodResolver(loginDetailsSchema),
    defaultValues: {
      username: '',
      password: '',
      confirmPassword: '',
    },
  });

  const { user, isLoading } = useUser();
  const { updateLoginDetails, removeAccount } = useUserProfile(form.setError);

  useEffect(() => {
    if (user) {
      form.reset({
        username: user.username,
        password: '',
        confirmPassword: '',
      });
    }
  }, [user, form]);

  const onSubmit = (formData: LoginDetailsSchema) => {
    const payload = {
      username: formData.username === user?.username ? undefined : formData.username,
      password: formData.password || undefined,
    };

    updateLoginDetails.mutate(payload);
  };

  const handleAccountRemove = useCallback(() => {
    removeAccount.mutate();
  }, [removeAccount]);

  return (
    <section className="grid w-full grid-cols-1 gap-4 max-w-md">
      <TabContent
        description="Wprowadź nowe dane, aby zaktualizować swoje konto."
        error={null}
        isError={!user}
        isLoading={isLoading}
        isPublic={true}
        title="Edytuj dane logowania"
      >
        <Form {...form}>
          <form className="grid gap-4 w-full" onSubmit={form.handleSubmit(onSubmit)}>
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
                    Nazwa użytkownika musi mieć od 6 do 16 znaków i może zawierać tylko litery i
                    cyfry.
                  </FormDescription>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold">Nowe hasło</FormLabel>
                  <FormControl>
                    <PasswordInput
                      {...field}
                      autoComplete="off"
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
                  <FormLabel className="font-semibold">Potwierdź nowe hasło</FormLabel>
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

            <TabButtons
              disabled={!form.formState.isDirty}
              isLoading={updateLoginDetails.isPending}
              onReset={() => form.reset()}
            />
          </form>
        </Form>
      </TabContent>
      <RemoveAccountCard
        isPending={removeAccount.isPending}
        onDelete={handleAccountRemove}
        username={user!.username}
      />
    </section>
  );
}

export default LoginDetailsTab;
