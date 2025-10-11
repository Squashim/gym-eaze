import { useUserProfile } from '@/hooks/query/user/use-user-profile';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Form, FormControl, FormField, FormItem, FormLabel } from '@/components/ui/form';
import { Gender } from '@/constants/enums/gender';
import useGetFitnessDetails from '@/hooks/query/user/use-get-fitness-details';
import Typography from '@/components/ui/typography';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import NumberInput from '@/components/ui/number-input';
import { fitnessDetailsDefaultValues } from '@/constants/defaults/forms';
import { USER_PROFILE } from '@/constants/user';
import {
  fitnessDetailsSchema,
  type FitnessDetailsSchema,
} from '@/schemas/user/fitness-details-schema';
import { TabButtons, TabContent, TabDisclaimer } from '@/components/ui/reusable-tabs';

function FitnessDetailsTab() {
  const form = useForm<FitnessDetailsSchema>({
    resolver: zodResolver(fitnessDetailsSchema),
    defaultValues: fitnessDetailsDefaultValues,
  });

  const { data: fitnessDetails, isLoading, isError, error } = useGetFitnessDetails();
  const { updateFitnessDetails } = useUserProfile();

  useEffect(() => {
    if (fitnessDetails) {
      form.reset({
        age: fitnessDetails.age,
        weight: fitnessDetails.weight,
        height: fitnessDetails.height,
        gender: fitnessDetails.gender,
      });
    }
  }, [fitnessDetails, form]);

  const onSubmit = (formData: FitnessDetailsSchema) => {
    const payload = {
      ...formData,
      age: formData.age === 0 || formData.age == null ? undefined : formData.age,
      weight: formData.weight === 0 || formData.weight == null ? undefined : formData.weight,
      height: formData.height === 0 || formData.height == null ? undefined : formData.height,
    };

    updateFitnessDetails.mutate(payload);
  };

  return (
    <section className="grid grid-cols-1 gap-4 max-w-2xl mx-auto">
      <TabContent
        description="Wprowadź nowe dane lub aktualizuj już istniejące. Wszystkie dane są opcjonalne, a ich widoczność zależy od Twoich ustawień prywatności. Domyślnie są publiczne."
        error={error}
        isError={isError}
        isLoading={isLoading}
        isPublic={fitnessDetails?.isFitnessDetailsPublic || false}
        title="Edytuj swoje informacje fizyczne"
      >
        <Form {...form}>
          <form
            className="grid gap-4 grid-cols-1 md:grid-cols-2 justify-items-center w-full"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <FormField
              control={form.control}
              name="gender"
              render={({ field }) => (
                <FormItem className="grid gap-2 w-full max-w-54">
                  <Typography
                    className="text-sm leading-none font-semibold select-none text-card-foreground my-0"
                    variant="lead"
                  >
                    Płeć
                  </Typography>
                  <FormControl>
                    <RadioGroup
                      className="flex flex-col"
                      name={field.name}
                      onValueChange={field.onChange}
                      value={field.value}
                    >
                      {Object.entries(Gender).map(([key, value]) => (
                        <FormItem className="flex items-center gap-3" key={key}>
                          <FormControl>
                            <RadioGroupItem value={value} />
                          </FormControl>
                          <FormLabel className="font-normal cursor-pointer">{value}</FormLabel>
                        </FormItem>
                      ))}
                    </RadioGroup>
                  </FormControl>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="age"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor={field.name}>
                    Wiek
                  </FormLabel>
                  <FormControl>
                    <NumberInput
                      className="w-32"
                      fillOnBlur={false}
                      id={field.name}
                      max={USER_PROFILE.AGE.MAX}
                      min={USER_PROFILE.AGE.MIN}
                      onChange={field.onChange}
                      placeholder="Nie podano"
                      step={1}
                      value={field.value ?? 0}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="height"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor={field.name}>
                    Wzrost (cm)
                  </FormLabel>
                  <FormControl>
                    <NumberInput
                      className="w-32"
                      fillOnBlur={false}
                      id={field.name}
                      max={USER_PROFILE.HEIGHT.MAX}
                      min={USER_PROFILE.HEIGHT.MIN}
                      onChange={field.onChange}
                      placeholder="Nie podano"
                      step={10}
                      suffix="cm"
                      value={field.value ?? 0}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="weight"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor={field.name}>
                    Waga (kg)
                  </FormLabel>
                  <FormControl>
                    <NumberInput
                      className="w-32"
                      fillOnBlur={false}
                      id={field.name}
                      max={USER_PROFILE.WEIGHT.MAX}
                      min={USER_PROFILE.WEIGHT.MIN}
                      onChange={field.onChange}
                      placeholder="Nie podano"
                      step={10}
                      suffix="kg"
                      value={field.value ?? 0}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            <TabButtons
              className="ml-auto"
              disabled={!form.formState.isDirty}
              isLoading={updateFitnessDetails.isPending}
              onReset={() => form.reset()}
            />
          </form>
        </Form>
      </TabContent>
      <TabDisclaimer
        description="Jeśli wprowadzisz informacje fizyczne domyślnie będą one widoczne na Twoim profilu. Możesz to zmienić w ustawieniach prywatności. Dzięki wprowadzeniu tych informacji w statystykach będzie widoczne Twoje BMI. Jego widoczność również zależy od Twoich ustawień prywatności."
        title="Informacje fizyczne"
      />
    </section>
  );
}

export default FitnessDetailsTab;
