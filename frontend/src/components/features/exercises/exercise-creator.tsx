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
import { useUnsavedChangesWarning } from '@/hooks/use-unsaved-changes-warning';
import { MultiSelect } from '@/components/ui/multi-select';
import { TagsInput } from '@/components/ui/tags-input';
import { cn } from '@/lib/utils';
import { Badge } from '@/components/ui/badge';
import { Globe2, Lock } from 'lucide-react';

const ExerciseCreator = () => {
  const steps = ['Podstawowe dane', 'Dodatkowe informacje', 'Podsumowanie'];
  const { form, step, nextStep, prevStep, totalSteps, refs, handleKeyDown } = UseStepForm({
    schema: exerciseSchema,
    defaultValues: {
      stepOne: {
        name: '',
        image: undefined,
        video: undefined,
        visibility: 'public',
        advancedLevel: 'easy',
      },
      stepTwo: {
        description: '',
        muscleGroup: [],
        equipment: [],
        hashtags: [],
      },
    },
    totalSteps: steps.length,
  });

  const { handleSubmit } = form;

  const onSubmit = (formData: ExerciseSchema) => {
    // TODO: add create exercise logic
    console.log(formData);
  };

  useUnsavedChangesWarning(form.formState.isDirty);

  return (
    <div className="flex flex-col gap-6 max-w-lg md:max-w-3xl w-full mx-auto my-4">
      <Typography variant="h1">Kreator ćwiczenia</Typography>
      <Stepper step={step} steps={steps} />
      <Card className="overflow-hidden min-h-[60dvh] flex flex-col items-center justify-center">
        <CardContent className="w-full">
          <Form {...form}>
            <form
              className="grid gap-y-4 md:grid-cols-2 md:gap-x-6"
              onKeyDown={handleKeyDown}
              onSubmit={handleSubmit(onSubmit)}
            >
              {step === 0 && <ExerciseCreatorStepOne />}
              {step === 1 && <ExerciseCreatorStepTwo />}
              {step === 2 && <ExerciseCreatorStepThree />}

              <StepActions
                exitLabel="Wyjdź z kreatora"
                nextStep={nextStep}
                prevStep={prevStep}
                refs={refs}
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
            <FormLabel className="font-semibold">Nazwa ćwiczenia*</FormLabel>
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
              Obraz poglądowy ćwiczenia*
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
              Filmik instruktażowy*
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
              Poziom trudności*
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
              <FormLabel className="font-semibold">Publiczne ćwiczenie*</FormLabel>
              <FormDescription>
                Inni użytkownicy będą mogli znaleźć i używać tego ćwiczenia.
              </FormDescription>
            </div>
            <FormControl>
              <Switch
                checked={field.value === 'public'}
                name={field.name}
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

  // TODO: change to fetch from API
  const equipmentList = [
    {
      label: 'Hantle',
      value: 'dumbbells',
      image: '/images/equipment/dumbbells.png',
    },
    {
      label: 'Sztanga',
      value: 'barbell',
      image: '/images/equipment/barbell.png',
    },
    { label: 'Ławka', value: 'bench', image: '/images/equipment/bench.png' },
    { label: 'Drążek', value: 'pull-up-bar', image: '/images/equipment/pull-up-bar.png' },
    { label: 'Gumy', value: 'bands', image: '/images/equipment/bands.png' },
    { label: 'Brak', value: 'none', image: '/images/equipment/none.png' },
    { label: 'Kettlebell', value: 'kettlebell', image: '/images/equipment/kettlebell.png' },
    { label: 'Piłka', value: 'ball', image: '/images/equipment/ball.png' },
    { label: 'Stepper', value: 'stepper', image: '/images/equipment/stepper.png' },
    { label: 'Orbitrek', value: 'elliptical', image: '/images/equipment/elliptical.png' },
    { label: 'Rower', value: 'bike', image: '/images/equipment/bike.png' },
  ];
  const muscleGroupsList = [
    { label: 'Klatka piersiowa', value: 'chest', image: '/images/muscle-groups/chest.png' },
    { label: 'Plecy', value: 'back', image: '/images/muscle-groups/back.png' },
    { label: 'Barki', value: 'shoulders', image: '/images/muscle-groups/shoulders.png' },
    { label: 'Biceps', value: 'biceps', image: '/images/muscle-groups/biceps.png' },
    { label: 'Triceps', value: 'triceps', image: '/images/muscle-groups/triceps.png' },
    { label: 'Nogi', value: 'legs', image: '/images/muscle-groups/legs.png' },
    { label: 'Brzuch', value: 'abs', image: '/images/muscle-groups/abs.png' },
    { label: 'Pośladki', value: 'glutes', image: '/images/muscle-groups/glutes.png' },
  ];

  return (
    <>
      <FormField
        control={control}
        name="stepTwo.description"
        render={({ field }) => (
          <FormItem className="md:col-span-2 w-full">
            <FormLabel className="font-semibold">Opis ćwiczenia*</FormLabel>
            <FormControl>
              <Textarea
                {...field}
                autoComplete="off"
                autoFocus
                className="resize-none min-h-44 scrollbar overflow-y-auto"
                maxLength={850}
                placeholder={`Opisz ćwiczenie i sposób wykonania np.\nKrok 1. Ustaw się w pozycji pompki...\nKrok 2. Zegnij ramiona...\n...`}
              />
            </FormControl>
            <FormMessage />
            <FormDescription>Maksymalna długość opisu to 850 znaków.</FormDescription>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepTwo.muscleGroup"
        render={({ field }) => (
          <FormItem>
            <FormLabel className="font-semibold">Grupy mięśniowe*</FormLabel>
            <FormControl>
              <MultiSelect
                maxCount={3}
                onValueChange={field.onChange}
                options={muscleGroupsList}
                placeholder="Wybierz grupy mięśniowe..."
                value={field.value}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepTwo.equipment"
        render={({ field }) => (
          <FormItem>
            <FormLabel className="font-semibold">Sprzęt ćwiczeniowy*</FormLabel>
            <FormControl>
              <MultiSelect
                hideSelectAll
                maxCount={3}
                onValueChange={field.onChange}
                options={equipmentList}
                placeholder="Wybierz sprzęt..."
                value={field.value}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepTwo.hashtags"
        render={({ field }) => (
          <FormItem className="md:col-span-2">
            <FormLabel className="font-semibold" htmlFor={field.name}>
              Hasztagi
            </FormLabel>
            <FormControl>
              <TagsInput
                id={field.name}
                onChange={field.onChange}
                placeholder="Dodaj hashtagi, np. biceps, triceps - użyj Entera lub przecinka"
                value={field.value}
              />
            </FormControl>
            <FormDescription>
              Hashtag może zawierać tylko małe litery i cyfry. Możesz dodać do 50 hashtagów.
            </FormDescription>
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
      <div className="md:col-span-2">
        <Typography className="md:col-span-2" variant="h3">
          Sprawdź poprawność danych
        </Typography>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
          <div className="flex flex-col gap-1 md:col-span-2">
            <Typography className="font-medium" variant="small">
              Nazwa ćwiczenia
            </Typography>
            <Typography className="px-1 bg-accent/50 rounded-md break-words" variant="p">
              {formData.stepOne.name}
            </Typography>
          </div>

          <div className="flex items-center gap-2">
            <Typography className="font-medium" variant="small">
              Poziom trudności:
            </Typography>
            <Badge
              className={cn(
                formData.stepOne.advancedLevel === 'easy'
                  ? 'bg-secondary text-secondary-foreground'
                  : formData.stepOne.advancedLevel === 'medium'
                    ? 'bg-yellow-600 dark:bg-yellow-400 text-accent-foreground dark:text-accent'
                    : 'bg-destructive',
              )}
            >
              {formData.stepOne.advancedLevel}
            </Badge>
          </div>

          <div className="flex items-center gap-2">
            <Typography className="font-medium" variant="small">
              Widoczność ćwiczenia:
            </Typography>
            <Badge className="flex h-auto w-fit">
              {formData.stepOne.visibility === 'public' ? <Globe2 /> : <Lock />}
              {formData.stepOne.visibility}
            </Badge>
          </div>

          <div className="flex flex-col gap-1 md:col-span-2 max-h-44 overflow-y-scroll scrollbar">
            <Typography className="font-medium" variant="small">
              Opis ćwiczenia
            </Typography>
            <Typography className="px-1 bg-accent/50 rounded-md break-words" variant="p">
              {formData.stepTwo.description}
            </Typography>
          </div>

          <div className="flex flex-col gap-1">
            <Typography className="font-medium" variant="small">
              Grupy mięśniowe
            </Typography>
            <div className="px-1 rounded-md flex flex-wrap gap-1">
              {formData.stepTwo.muscleGroup.map((muscle) => {
                return (
                  <Badge key={muscle} variant="outline">
                    {muscle}
                  </Badge>
                );
              })}
            </div>
          </div>

          <div className="flex flex-col gap-1">
            <Typography className="font-medium" variant="small">
              Wymagany sprzęt
            </Typography>
            <div className="px-1 rounded-md flex flex-wrap gap-1">
              {formData.stepTwo.equipment.map((equipment) => {
                return (
                  <Badge key={equipment} variant="outline">
                    {equipment}
                  </Badge>
                );
              })}
            </div>
          </div>

          <div className="flex flex-col gap-1 md:col-span-2 max-h-24 overflow-y-scroll scrollbar">
            <Typography className="font-medium" variant="small">
              Hasztagi
            </Typography>
            <div className="px-1 rounded-md flex flex-wrap gap-1">
              {formData.stepTwo.hashtags.map((hashtag) => {
                return (
                  <Badge key={hashtag} variant="outline">
                    {`#${hashtag}`}
                  </Badge>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
