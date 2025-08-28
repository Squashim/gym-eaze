import { useFormContext } from 'react-hook-form';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { FileInput } from '@/components/ui/file-input';
import { exerciseSchema, type ExerciseSchema } from '@/schemas/exercise-schema';
import Typography from '@/components/ui/typography';
import { Switch } from '@/components/ui/switch';
import { UseStepForm } from '@/hooks/use-step-form';
import Stepper from '@/components/ui/stepper';
import StepActions from '@/components/ui/step-actions';
import { Textarea } from '@/components/ui/textarea';

const ExerciseCreator = () => {
  const steps = ['Podstawowe dane', 'Dodatkowe informacje', 'Podsumowanie'];
  const { form, step, nextStep, prevStep, totalSteps } = UseStepForm({
    schema: exerciseSchema,
    defaultValues: {
      stepOne: {
        author: '',
        name: '',
        image: undefined,
        video: undefined,
        visibility: 'public',
        advancedLevel: 'easy',
      },
      stepTwo: {
        description: '',
        // muscleGroup: [],
        // equipment: [],
        // hashtags: [],
      },
    },
    totalSteps: steps.length,
  });

  const { handleSubmit } = form;

  const onSubmit = (formData: ExerciseSchema) => {
    console.log(formData);
  };

  return (
    <div className="flex flex-col gap-6 max-w-lg md:max-w-3xl w-full mx-auto my-4">
      <Typography variant="h1">Kreator ćwiczenia</Typography>
      <Stepper step={step} steps={steps} />
      <Card className="overflow-hidden min-h-[60dvh] flex flex-col items-center justify-center">
        <CardContent>
          <Form {...form}>
            <form
              className="grid gap-y-4 md:grid-cols-2 md:gap-x-6"
              onKeyDown={async (e) => {
                if (e.key === 'Enter') {
                  if (step < totalSteps - 1) {
                    e.preventDefault();
                    await nextStep();
                  }
                }
              }}
              onSubmit={handleSubmit(onSubmit)}
            >
              {step === 0 && <ExerciseCreatorStepOne />}
              {step === 1 && <ExerciseCreatorStepTwo />}
              {step === 2 && <ExerciseCreatorStepThree />}

              <StepActions
                exitLabel="Wyjdź z kreatora"
                nextStep={nextStep}
                prevStep={prevStep}
                step={step}
                submitLabel="Stwórz ćwiczenie"
                totalSteps={totalSteps}
              />
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
};

export default ExerciseCreator;

const ExerciseCreatorStepOne = () => {
  const { control } = useFormContext<ExerciseSchema>();

  return (
    <>
      <FormField
        control={control}
        name="stepOne.name"
        render={({ field }) => (
          <FormItem className="md:col-span-2">
            <FormLabel className="font-semibold">Nazwa ćwiczenia</FormLabel>
            <FormControl>
              <Input
                {...field}
                autoComplete="off"
                autoFocus
                maxLength={150}
                placeholder="Podaj nazwę ćwiczenia"
                type="text"
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.image"
        render={({ field, fieldState }) => (
          <FormItem>
            <FormLabel className="font-semibold" htmlFor="image">
              Obraz poglądowy ćwiczenia
            </FormLabel>
            <FormControl>
              <FileInput
                accept="image/jpg, image/webp, image/png"
                id="image"
                invalid={!!fieldState.error}
                onChange={field.onChange}
                value={field.value}
                variant="image"
              />
            </FormControl>
            <FormMessage />
            <FormDescription>
              Maksymalny rozmiar obrazu to 5MB. Obsługiwane typy: .jpg, .png, .webp.
            </FormDescription>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.video"
        render={({ field, fieldState }) => (
          <FormItem>
            <FormLabel className="font-semibold" htmlFor="video">
              Filmik instruktażowy
            </FormLabel>
            <FormControl>
              <FileInput
                accept="video/mp4, video/webm"
                id="video"
                invalid={!!fieldState.error}
                onChange={field.onChange}
                value={field.value}
                variant="video"
              />
            </FormControl>
            <FormMessage />
            <FormDescription>
              Maksymalny rozmiar filmiku to 20MB. Obsługiwane typy: .mp4, .webm.
            </FormDescription>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.advancedLevel"
        render={({ field }) => (
          <FormItem className="flex flex-col justify-between rounded-md border p-3">
            <Typography
              className="text-sm leading-none font-semibold select-none text-card-foreground my-0"
              variant="lead"
            >
              Poziom trudności
            </Typography>
            <FormControl className="mt-1">
              <RadioGroup
                className="flex flex-col "
                defaultValue={field.value}
                name={field.name}
                onValueChange={field.onChange}
              >
                <FormItem className="flex items-center gap-3">
                  <FormControl>
                    <RadioGroupItem value="easy" />
                  </FormControl>
                  <FormLabel className="font-normal cursor-pointer">Początkujący</FormLabel>
                </FormItem>
                <FormItem className="flex items-center gap-3">
                  <FormControl>
                    <RadioGroupItem value="medium" />
                  </FormControl>
                  <FormLabel className="font-normal cursor-pointer">Średniozaawansowany</FormLabel>
                </FormItem>
                <FormItem className="flex items-center gap-3">
                  <FormControl>
                    <RadioGroupItem value="hard" />
                  </FormControl>
                  <FormLabel className="font-normal cursor-pointer">Zaawansowany</FormLabel>
                </FormItem>
              </RadioGroup>
            </FormControl>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.visibility"
        render={({ field }) => (
          <FormItem className="flex flex-row items-center justify-between rounded-md border p-3">
            <div className="flex flex-col gap-1">
              <FormLabel className="font-semibold">Publiczne ćwiczenie</FormLabel>
              <FormDescription>
                Inni użytkownicy będą mogli przeglądać i korzystać z Twojego ćwiczenia.
              </FormDescription>
            </div>
            <FormControl>
              <Switch
                checked={field.value === 'public'}
                onCheckedChange={(checked) => field.onChange(checked ? 'public' : 'private')}
              />
            </FormControl>
          </FormItem>
        )}
      />
    </>
  );
};

const ExerciseCreatorStepTwo = () => {
  const { control } = useFormContext<ExerciseSchema>();

  return (
    <>
      <FormField
        control={control}
        name="stepTwo.description"
        render={({ field }) => (
          <FormItem className="md:col-span-2">
            <FormLabel className="font-semibold">Opis ćwiczenia</FormLabel>
            <FormControl>
              <Textarea
                {...field}
                autoComplete="off"
                autoFocus
                className="resize-none"
                maxLength={850}
                placeholder="Opisz ćwiczenie i sposób wykonania np. Krok 1. Ustaw się w pozycji pompki..."
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
    </>
  );
};

const ExerciseCreatorStepThree = () => {
  const { watch } = useFormContext<ExerciseSchema>();
  const formData = watch();

  return (
    <>
      <div>{JSON.stringify(formData, null, 2)}</div>
    </>
  );
};
