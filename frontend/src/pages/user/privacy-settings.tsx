import { TabButtons, TabContent } from '@/components/ui/reusable-tabs';
import PageLayout from '@/components/layout/page';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
} from '@/components/ui/form';
import { Switch } from '@/components/ui/switch';
import { privacySettingsDefaultValues } from '@/constants/defaults/forms';
import { privacySettingsMapping } from '@/constants/mappings/user-settings';
import useGetPrivacySettings from '@/hooks/query/user/use-get-privacy-settings';
import { useUserProfile } from '@/hooks/query/user/use-user-profile';
import usePageTitle from '@/hooks/utils/use-page-title';
import {
  privacySettingsSchema,
  type PrivacySettingsSchema,
} from '@/schemas/user/privacy-settings-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';

function PrivacySettings() {
  const form = useForm<PrivacySettingsSchema>({
    resolver: zodResolver(privacySettingsSchema),
    defaultValues: privacySettingsDefaultValues,
  });
  const { data: privacySettings, isLoading, isError, error } = useGetPrivacySettings();
  const { updatePrivacySettings } = useUserProfile();
  usePageTitle('Ustawienia prywatności');

  useEffect(() => {
    if (privacySettings && privacySettings.data) {
      form.reset({
        isPersonalDetailsPublic: privacySettings.data.isPersonalDetailsPublic,
        isFitnessDetailsPublic: privacySettings.data.isFitnessDetailsPublic,
        isBadgesDetailsPublic: privacySettings.data.isBadgesDetailsPublic,
        isGoalPublic: privacySettings.data.isGoalPublic,
        isTrainingStatisticsPublic: privacySettings.data.isTrainingStatisticsPublic,
      });
    }
  }, [privacySettings, form]);

  const onSubmit = (formData: PrivacySettingsSchema) => {
    updatePrivacySettings.mutate({ data: formData });
  };

  return (
    <PageLayout
      className="mx-auto w-auto"
      description="Zarządzaj, kto może zobaczyć poszczególne informacje na Twoim profilu"
      title="Ustawienia prywatności"
    >
      <section className="grid w-full grid-cols-1 gap-4 max-w-4xl">
        <TabContent
          description="Zmień ustawieni prywatności, aby dostosować widoczność swoich danych. Możesz ukryć swoje dane przed innymi użytkownikami lub udostępnić je wszystkim odwiedzającym Twój profil."
          error={error}
          isError={isError}
          isLoading={isLoading}
          title="Zmień ustawienia prywatności"
        >
          <Form {...form}>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4 w-full"
              onSubmit={form.handleSubmit(onSubmit)}
            >
              {privacySettingsMapping.map((setting) => (
                <FormField
                  control={form.control}
                  key={setting.name}
                  name={setting.name}
                  render={({ field }) => (
                    <FormItem className="flex flex-row items-center justify-between rounded-md border p-3 gap-3">
                      <div className="flex flex-col gap-1">
                        <FormLabel className="font-semibold">{setting.label}</FormLabel>
                        <FormDescription>{setting.description}</FormDescription>
                      </div>
                      <FormControl>
                        <Switch
                          checked={field.value}
                          name={field.name}
                          onCheckedChange={field.onChange}
                        />
                      </FormControl>
                    </FormItem>
                  )}
                />
              ))}

              <TabButtons
                disabled={!form.formState.isDirty}
                isLoading={updatePrivacySettings.isPending}
                onReset={() => form.reset()}
              />
            </form>
          </Form>
        </TabContent>
      </section>
    </PageLayout>
  );
}

export default PrivacySettings;
