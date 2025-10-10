import { Country } from '@/constants/countries';
import useGetPersonalDetails from '@/hooks/query/user/use-get-personal-details';
import { useUserProfile } from '@/hooks/query/user/use-user-profile';
import { personalDetailsSchema, type PersonalDetailsSchema } from '@/schemas/user-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { TabButtons, TabContent, TabDisclaimer } from './tabs-ui';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import CountryDropdown from '@/components/ui/country-dropdown';
import { personalDetailsDefaultValues } from '@/constants/forms';
import { USER_PROFILE } from '@/constants/user';

function PersonalDetailsTab() {
  const form = useForm<PersonalDetailsSchema>({
    resolver: zodResolver(personalDetailsSchema),
    defaultValues: personalDetailsDefaultValues,
  });

  const { data: personalDetails, isLoading, isError, error } = useGetPersonalDetails();
  const { updatePersonalDetails } = useUserProfile();

  const countries = useMemo(() => {
    return Object.entries(Country).map(([key, label]) => ({
      label,
      value: key,
    }));
  }, []);

  useEffect(() => {
    if (personalDetails) {
      form.reset({
        firstName: personalDetails.firstName,
        lastName: personalDetails.lastName,
        country: personalDetails.country || Country.NONE,
      });
    }
  }, [personalDetails, form]);

  const onSubmit = (formData: PersonalDetailsSchema) => {
    updatePersonalDetails.mutate(formData);
  };

  return (
    <section className="grid w-full grid-cols-1 gap-4 max-w-2xl mx-auto">
      <TabContent
        description="Wprowadź nowe dane lub aktualizuj już istniejące. Wszystkie dane są opcjonalne, a ich widoczność zależy od Twoich ustawień prywatności. Domyślnie są publiczne."
        error={error}
        isError={isError}
        isLoading={isLoading}
        isPublic={personalDetails?.isPersonalDetailsPublic || false}
        title="Edytuj dane personalne"
      >
        <Form {...form}>
          <form
            className="grid gap-4 grid-cols-1 md:grid-cols-2"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <FormField
              control={form.control}
              name="firstName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold">Imię</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      autoComplete="given-name"
                      maxLength={USER_PROFILE.FIRST_NAME_MAX_LENGTH}
                      placeholder="Jan"
                      type="text"
                      value={field.value ?? ''}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="lastName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold">Nazwisko</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      autoComplete="family-name"
                      maxLength={USER_PROFILE.LAST_NAME_MAX_LENGTH}
                      placeholder="Kowalski"
                      type="text"
                      value={field.value ?? ''}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Separator className="col-span-full" />

            <FormField
              control={form.control}
              name="country"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor="country-dropdown">
                    Kraj
                  </FormLabel>
                  <FormControl>
                    <CountryDropdown
                      id="country-dropdown"
                      onChange={field.onChange}
                      options={countries}
                      value={field.value || Country.NONE}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <TabButtons
              disabled={!form.formState.isDirty}
              isLoading={updatePersonalDetails.isPending}
              onReset={() => form.reset()}
            />
          </form>
        </Form>
      </TabContent>
      <TabDisclaimer
        description="Jeśli wprowadzisz dane personalne domyślnie będą one widoczne na Twoim profilu. Możesz to zmienić w ustawieniach prywatności. Pamiętaj, że dzięki tym danym, inni użytkownicy będą Cię lepiej rozpoznawać."
        title="Dane personalne"
      />
    </section>
  );
}

export default PersonalDetailsTab;
