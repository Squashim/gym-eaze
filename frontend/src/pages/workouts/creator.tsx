import DaysList from '@/components/features/workouts/days/days-list';
import PageLayout from '@/components/layout/page';
import { Card, CardContent } from '@/components/ui/card';
import { FileInput } from '@/components/ui/file-input';
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
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import StepActions from '@/components/ui/step-actions';
import Stepper from '@/components/ui/stepper';
import { Switch } from '@/components/ui/switch';
import { TagsInput } from '@/components/ui/tags-input';
import { Textarea } from '@/components/ui/textarea';
import Typography from '@/components/ui/typography';
import { AdvancedLevel } from '@/constants/advanced-level';
import { workoutCreatorConstants } from '@/constants/forms';
import { Goal } from '@/constants/goals';
import { Visibility } from '@/constants/visibility';
import usePageTitle from '@/hooks/utils/use-page-title';
import { useStepForm } from '@/hooks/use-step-form';
import {
  workoutSchema,
  type TrainingDaySchema,
  type WorkoutSchema,
} from '@/schemas/workout-schema';
import { useEffect, useMemo, useState } from 'react';
import { useFormContext } from 'react-hook-form';
import ActiveDay from '@/components/features/workouts/active-day';
import { IconInfoField, InfoField } from '@/components/ui/creator-info-fields';
import { AdvancedLevelBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import { Badge } from '@/components/ui/badge';
import { calculateWorkoutTime } from '@/lib/utils';
import {
  parseDayLabel,
  parseExerciseLabel,
  parseTimeBrackets,
  parseWorkoutSetsLabel,
} from '@/lib/parsers';
import { BicepsFlexed, CalendarDays, Clock, Flame } from 'lucide-react';
import { useWorkout } from '@/hooks/query/workouts/use-workout';
import type { CreateWorkoutPayload } from '@/types/workout';
import HashtagList from '@/components/ui/hashtag-list';
import { useUnsavedChangesWarning } from '@/hooks/utils/use-unsaved-changes-warning';
import { toast } from 'sonner';

interface WorkoutCreatorProps {
  mode: 'create' | 'edit';
}

function WorkoutCreator({ mode }: WorkoutCreatorProps) {
  const { form, step, nextStep, prevStep, totalSteps, refs, handleKeyDown } = useStepForm({
    schema: workoutSchema,
    defaultValues: workoutCreatorConstants.defaultValues,
    totalSteps: workoutCreatorConstants.steps.length,
  });

  const { createWorkout } = useWorkout();
  const { handleSubmit } = form;

  const isEditMode = mode === 'edit';

  const { onCancel, bypassBlocker } = useUnsavedChangesWarning(form.formState.isDirty);

  usePageTitle(isEditMode ? 'Edytor planu treningowego' : 'Kreator planu treningowego');

  useEffect(() => {
    const daysError = form.formState.errors?.stepTwo?.days;
    if (daysError && typeof daysError.message === 'string') {
      toast.error(daysError.message);
    } else if (Array.isArray(daysError)) {
      toast.error('Upewnij się, że każdy dzień treningowy jest poprawnie wypełniony.');
    }
  }, [form.formState.errors?.stepTwo?.days]);

  const handleCreate = (formData: WorkoutSchema) => {
    const formattedData = {
      ...formData.stepOne,
      ...formData.stepTwo,
    } as CreateWorkoutPayload;

    bypassBlocker();
    createWorkout.mutate(formattedData);
  };

  const onSubmit = (formData: WorkoutSchema) => {
    if (isEditMode) {
      console.log('TODO');
    } else {
      handleCreate(formData);
    }
  };

  return (
    <PageLayout
      className="max-w-lg md:max-w-3xl mx-auto"
      title={isEditMode ? 'Edytor planu treningowego' : 'Kreator planu treningowego'}
      titleClassName="text-center"
    >
      <Stepper step={step} steps={workoutCreatorConstants.steps} />
      <Card className="overflow-hidden min-h-[60dvh] flex flex-col items-center justify-start">
        <CardContent className="w-full">
          <Form {...form}>
            <form
              className="grid gap-y-4 md:grid-cols-3 md:gap-x-6 mb-8"
              onKeyDown={handleKeyDown}
              onSubmit={handleSubmit(onSubmit)}
            >
              {step === 0 && <WorkoutCreatorStepOne />}
              {step === 1 && <WorkoutCreatorStepTwo />}
              {step === 2 && <WorkoutCreatorStepThree />}

              <StepActions
                exitLabel={isEditMode ? 'Wyjdź z edytora' : 'Wyjdź z kreatora'}
                fixed
                isSubmitting={createWorkout.isPending}
                nextStep={nextStep}
                onCancel={onCancel}
                prevStep={prevStep}
                refs={refs}
                step={step}
                submitDisabled={isEditMode && !form.formState.isDirty}
                submitLabel={isEditMode ? 'Edytuj plan' : 'Stwórz plan'}
                submittingLabel={isEditMode ? 'Edytowanie...' : 'Tworzenie...'}
                totalSteps={totalSteps}
              />
            </form>
          </Form>
        </CardContent>
      </Card>
    </PageLayout>
  );
}

export default WorkoutCreator;

function WorkoutCreatorStepOne() {
  const { control } = useFormContext<WorkoutSchema>();

  return (
    <>
      <FormField
        control={control}
        name="stepOne.name"
        render={({ field }) => (
          <FormItem className="md:col-span-2">
            <FormLabel className="font-semibold">Nazwa planu*</FormLabel>
            <FormControl>
              <Input
                {...field}
                autoComplete="off"
                autoFocus
                maxLength={150}
                placeholder="Podaj nazwę planu treningowego"
                type="text"
              />
            </FormControl>
            <FormMessage />
            <FormDescription>Maksymalna długość nazwy to 150 znaków.</FormDescription>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.goal"
        render={({ field }) => (
          <FormItem className="self-start">
            <FormLabel className="font-semibold" htmlFor={field.name}>
              Cel treningowy*
            </FormLabel>
            <FormControl>
              <Select name={field.name} onValueChange={field.onChange} value={field.value}>
                <SelectTrigger className="w-full md:max-w-[223px]" id={field.name}>
                  <SelectValue placeholder="Wybierz cel treningowy..." />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(Goal).map(([key, value]) => (
                    <SelectItem key={key} value={value}>
                      {value}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </FormControl>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.image"
        render={({ field, fieldState }) => (
          <FormItem className="md:col-span-3">
            <FormLabel className="font-semibold" htmlFor="image">
              Miniaturka planu*
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
                name={field.name}
                onValueChange={field.onChange}
                value={field.value}
              >
                {Object.entries(AdvancedLevel).map(([key, value]) => (
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
        control={control}
        name="stepOne.visibility"
        render={({ field }) => (
          <FormItem className="flex flex-row items-center justify-between rounded-md border p-3 md:col-span-2">
            <div className="flex flex-col gap-1">
              <FormLabel className="font-semibold">Publiczny plan treningowy*</FormLabel>
              <FormDescription>
                Inni użytkownicy będą mogli wyszukać i korzystać z tego planu treningowego.
              </FormDescription>
            </div>
            <FormControl>
              <Switch
                checked={field.value === Visibility.public}
                name={field.name}
                onCheckedChange={(checked) =>
                  field.onChange(checked ? Visibility.public : Visibility.private)
                }
              />
            </FormControl>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.description"
        render={({ field }) => (
          <FormItem className="md:col-span-3 w-full">
            <FormLabel className="font-semibold">Opis planu</FormLabel>
            <FormControl>
              <Textarea
                {...field}
                autoComplete="off"
                className="resize-none min-h-48 max-h-48 scrollbar overflow-y-auto"
                maxLength={850}
                placeholder='Opisz swój plan treningowy. Napisz do kogo jest skierowany, jakie cele pomaga osiągnąć itp. (np. "Plan treningowy dla początkujących, którzy chcą zbudować masę mięśniową i poprawić ogólną kondycję fizyczną.")'
              />
            </FormControl>
            <FormMessage />
            <FormDescription>Maksymalna długość opisu to 850 znaków.</FormDescription>
          </FormItem>
        )}
      />

      <FormField
        control={control}
        name="stepOne.hashtags"
        render={({ field }) => (
          <FormItem className="md:col-span-3">
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
              Hasztag może zawierać tylko małe litery i cyfry. Możesz dodać do 50 hasztagów.
            </FormDescription>
          </FormItem>
        )}
      />
    </>
  );
}

function WorkoutCreatorStepTwo() {
  const [selectedDay, setSelectedDay] = useState<TrainingDaySchema | null>(null);
  const [selectedTab, setSelectedTab] = useState<string>('week-1');

  if (selectedDay) {
    return <ActiveDay day={selectedDay} onDaySelect={setSelectedDay} />;
  }

  return (
    <DaysList onDaySelect={setSelectedDay} onTabSelect={setSelectedTab} selectedTab={selectedTab} />
  );
}

function WorkoutCreatorStepThree() {
  const { watch } = useFormContext<WorkoutSchema>();
  const formData = watch();

  const { totalDays, totalExercises, totalSets, avgDailyWorkoutTime } = useMemo(() => {
    const totalDays = formData.stepTwo.days.length;
    const totalExercises = formData.stepTwo.days.reduce(
      (acc, day) => acc + day.exercises.length,
      0,
    );
    const totalSets = formData.stepTwo.days.reduce(
      (acc, day) =>
        acc +
        day.exercises.reduce((exerciseAcc, exercise) => exerciseAcc + exercise.sets.length, 0),
      0,
    );
    const totalWorkoutTime = formData.stepTwo.days.reduce(
      (acc, day) => acc + calculateWorkoutTime(day),
      0,
    );
    const avgDailyWorkoutTime = totalDays ? Math.round(totalWorkoutTime / totalDays) : 0;

    return { totalDays, totalExercises, totalSets, avgDailyWorkoutTime };
  }, [formData.stepTwo.days]);

  return (
    <div className="md:col-span-full">
      <Typography className="md:col-span-full" variant="h3">
        Sprawdź poprawność danych
      </Typography>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
        <InfoField fullWidth label="Nazwa planu treningowego">
          <Typography
            className="break-words px-2 py-1 rounded-md bg-accent-foreground/10"
            variant="p"
          >
            {formData.stepOne.name}
          </Typography>
        </InfoField>

        <InfoField label="Poziom trudności">
          <AdvancedLevelBadge advancedLevel={formData.stepOne.advancedLevel} />
        </InfoField>

        <InfoField label="Widoczność planu">
          <VisibilityBadge type="Workout" visibility={formData.stepOne.visibility} />
        </InfoField>

        <InfoField label="Cel treningowy">
          <Badge className="bg-accent text-accent-foreground shadow-sm min-h-7">
            {formData.stepOne.goal}
          </Badge>
        </InfoField>

        {formData.stepOne.description && (
          <InfoField fullWidth label="Opis planu">
            <Typography
              className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent-foreground/10"
              variant="p"
            >
              {formData.stepOne.description}
            </Typography>
          </InfoField>
        )}

        <div className="flex flex-col gap-1 col-span-full">
          <Typography className="font-semibold" variant="small">
            Plan zawiera:
          </Typography>
          <div className="flex flex-wrap gap-2 items-center">
            <IconInfoField icon={CalendarDays} label={parseDayLabel(totalDays)} />
            <IconInfoField icon={BicepsFlexed} label={parseExerciseLabel(totalExercises)} />
            <IconInfoField icon={Flame} label={parseWorkoutSetsLabel(totalSets)} />
          </div>
        </div>

        <InfoField fullWidth label="Szacowany czas na codzienny trening">
          <IconInfoField icon={Clock} label={parseTimeBrackets(avgDailyWorkoutTime)} />
        </InfoField>

        <HashtagList hashtags={formData.stepOne.hashtags} />
      </div>
    </div>
  );
}
