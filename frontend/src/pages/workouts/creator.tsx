import DaysList from '@/components/features/workouts/days-list';
import PageLayout from '@/components/layout/page';
import { Button } from '@/components/ui/button';
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
import usePageTitle from '@/hooks/use-page-title';
import { useStepForm } from '@/hooks/use-step-form';
import { getWeeks } from '@/lib/utils';
import {
  workoutSchema,
  type TrainingDaySchema,
  type WorkoutSchema,
} from '@/schemas/workout-schema';
import { ArrowLeft, Plus } from 'lucide-react';
import { useCallback, useMemo, useState } from 'react';
import { useFormContext } from 'react-hook-form';

interface WorkoutCreatorProps {
  mode: 'create' | 'edit';
}

function WorkoutCreator({ mode }: WorkoutCreatorProps) {
  const { form, step, nextStep, prevStep, totalSteps, refs, handleKeyDown } = useStepForm({
    schema: workoutSchema,
    defaultValues: workoutCreatorConstants.defaultValues,
    totalSteps: workoutCreatorConstants.steps.length,
  });

  const { handleSubmit } = form;

  const isEditMode = mode === 'edit';

  usePageTitle(isEditMode ? 'Edytor planu treningowego' : 'Kreator planu treningowego');

  const onSubmit = (formData: WorkoutSchema) => {
    if (isEditMode) {
      console.log('Edit mode - form data:', formData);
      // handleUpdate(formData);
    } else {
      console.log('Create mode - form data:', formData);
      // handleCreate(formData);
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

              <StepActions
                exitLabel={isEditMode ? 'Wyjdź z edytora' : 'Wyjdź z kreatora'}
                fixed
                isDirty={form.formState.isDirty}
                isSubmitting={false}
                nextStep={nextStep}
                prevStep={prevStep}
                refs={refs}
                step={step}
                submitDisabled={false}
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
        name="stepOne.trainingGoal"
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
  const { control } = useFormContext<WorkoutSchema>();

  return (
    <FormField
      control={control}
      name="stepTwo.days"
      render={({ field }) => (
        <FormItem className="md:col-span-full">
          <FormControl>
            <DaysInput onChange={field.onChange} value={field.value} />
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

interface DaysInputProps {
  value: TrainingDaySchema[];
  onChange: (days: TrainingDaySchema[]) => void;
}

function DaysInput({ value, onChange }: DaysInputProps) {
  const [activeDayIndex, setActiveDayIndex] = useState<number | null>(null);
  const weeks = useMemo(() => getWeeks(value), [value]);

  const handleTotalDaysChange = useCallback(
    (newTotal: number) => {
      if (newTotal > value.length) {
        const additionalDays = Array.from(
          {
            length: newTotal - value.length,
          },
          (_, i) => ({
            name: `Dzień ${value.length + i + 1}`,
            isRestDay: false,
            exercises: [],
          }),
        );
        onChange([...value, ...additionalDays]);
      } else if (newTotal < value.length) {
        onChange(value.slice(0, newTotal));
      }
    },
    [value, onChange],
  );

  if (activeDayIndex !== null) {
    return (
      <ActiveDayView
        day={value[activeDayIndex]}
        dayIndex={activeDayIndex}
        setActiveDayIndex={setActiveDayIndex}
      />
    );
  }

  return (
    <DaysList
      handleTotalDaysChange={handleTotalDaysChange}
      setActiveDayIndex={setActiveDayIndex}
      totalDays={value.length}
      weeks={weeks}
    />
  );
}

type ActiveDayViewProps = {
  day: TrainingDaySchema;
  dayIndex: number;
  setActiveDayIndex: (index: number | null) => void;
};

const ActiveDayView = ({ day, dayIndex, setActiveDayIndex }: ActiveDayViewProps) => {
  const { control } = useFormContext<WorkoutSchema>();

  return (
    <div className="md:col-span-3 flex flex-col gap-4">
      <Button className="w-fit" onClick={() => setActiveDayIndex(null)} variant="outline">
        <ArrowLeft />
        Powrót do listy dni
      </Button>

      <FormField
        control={control}
        name={`stepTwo.days.${dayIndex}.name`}
        render={({ field }) => (
          <FormItem>
            <FormLabel className="font-semibold">Nazwa dnia</FormLabel>
            <FormControl>
              <Input
                {...field}
                autoComplete="off"
                autoFocus
                maxLength={150}
                placeholder="Podaj nazwę dnia treningowego"
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
        name={`stepTwo.days.${dayIndex}.isRestDay`}
        render={({ field }) => (
          <FormItem className="flex flex-row items-center justify-between rounded-md border p-3 md:col-span-2">
            <div className="flex flex-col gap-1">
              <FormLabel className="font-semibold">Dzień wolny od ćwiczeń</FormLabel>
              <FormDescription>
                Ustawienie dnia jako wolny, zablokuje możliwość dodawania do niego ćwiczeń.
              </FormDescription>
            </div>
            <FormControl>
              <Switch
                checked={field.value}
                name={field.name}
                onCheckedChange={(checked) => field.onChange(checked)}
              />
            </FormControl>
          </FormItem>
        )}
      />

      <Button disabled={day.isRestDay} variant="secondary">
        <Plus />
        Dodaj ćwiczenia
      </Button>
    </div>
  );
};
