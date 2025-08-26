import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Button } from '@/components/ui/button';
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
import { toast } from 'sonner';
import { cn } from '@/lib/utils';
import { Input } from '@/components/ui/input';
import { FileInput } from '@/components/ui/file-input';
import { exerciseSchema, type ExerciseSchema } from '@/schemas/exercise-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { ChevronLeft, ChevronRight, X } from 'lucide-react';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';

const ExerciseCreator = () => {
  const [step, setStep] = useState(0);
  const totalSteps = 3;

  const form = useForm<ExerciseSchema>({
    resolver: zodResolver(exerciseSchema),
    defaultValues: {
      name: '',
      description: '',
      image: undefined,
      video: undefined,
      visibility: 'public',
      advancedLevel: 'easy',
      muscleGroup: [],
      equipment: [],
      hashtags: [],
    },
  });

  const stepFields: Array<(keyof ExerciseSchema)[]> = [
    ['name', 'image', 'video', 'advancedLevel'],
    ['description'],
    [],
  ];

  const { handleSubmit, reset } = form;

  const onSubmit = async (formData: ExerciseSchema) => {
    const isValid = await form.trigger(stepFields[step]);

    if (!isValid) return;

    if (step < totalSteps - 1) {
      setStep(step + 1);
    } else {
      console.log(formData);
      setStep(0);
      reset();

      toast.success('Form successfully submitted');
    }
  };

  const handleBack = () => {
    if (step > 0) {
      setStep(step - 1);
    }
  };

  const renderActionBtns = () => {
    return (
      <div className="flex justify-between items-center gap-4">
        {step !== 0 && (
          <Button onClick={handleBack} size="lg" type="button" variant="outline">
            <ChevronLeft className="size-5" />
            Cofnij
          </Button>
        )}

        <div className="flex items-center ml-auto gap-3">
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                aria-describedby="exit-exercise-creator-tooltip-description"
                className="size-10"
                size="icon"
                type="button"
                variant="outline"
              >
                <X className="size-5" />
              </Button>
            </TooltipTrigger>
            <TooltipContent id="exit-exercise-creator-tooltip-description">
              <Typography variant="small">Wyjdź z kreatora</Typography>
            </TooltipContent>
          </Tooltip>

          <Button size="lg" type="submit">
            {step === 2 ? 'Stwórz ćwiczenie' : 'Kolejny etap'}
            {step !== 2 && <ChevronRight className="size-5" />}
          </Button>
        </div>
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-6 max-w-lg md:max-w-2xl w-full mx-auto my-4">
      <Typography variant="h1">Kreator ćwiczenia</Typography>
      <div className="flex items-center justify-center">
        {Array.from({ length: totalSteps }).map((_, index) => (
          <div className="flex items-center" key={index}>
            <div
              className={cn(
                'size-10 rounded-full transition-all duration-300 ease-in-out flex items-center justify-center shadow-xs border bg-input/30',
                index === step && ' border-primary w-24',
                index < step && 'bg-secondary border-border text-secondary-foreground',
              )}
            >
              <Typography className="font-semibold" variant="small">
                {index + 1}
              </Typography>
            </div>
            {index < totalSteps - 1 && (
              <div className={cn('w-8 h-0.5', index < step ? 'bg-secondary' : 'bg-border')} />
            )}
          </div>
        ))}
      </div>
      <Card>
        <CardContent>
          {step === 0 && (
            <Form {...form}>
              <form className="grid gap-y-4" onSubmit={handleSubmit(onSubmit)}>
                <FormField
                  control={form.control}
                  key="name"
                  name="name"
                  render={({ field }) => (
                    <FormItem>
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
                  control={form.control}
                  name="image"
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
                  control={form.control}
                  name="video"
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
                  control={form.control}
                  name="advancedLevel"
                  render={({ field }) => (
                    <FormItem>
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
                            <FormLabel className="font-normal cursor-pointer">
                              Początkujący
                            </FormLabel>
                          </FormItem>
                          <FormItem className="flex items-center gap-3">
                            <FormControl>
                              <RadioGroupItem value="medium" />
                            </FormControl>
                            <FormLabel className="font-normal cursor-pointer">
                              Średniozaawansowany
                            </FormLabel>
                          </FormItem>
                          <FormItem className="flex items-center gap-3">
                            <FormControl>
                              <RadioGroupItem value="hard" />
                            </FormControl>
                            <FormLabel className="font-normal cursor-pointer">
                              Zaawansowany
                            </FormLabel>
                          </FormItem>
                        </RadioGroup>
                      </FormControl>
                    </FormItem>
                  )}
                />

                {renderActionBtns()}
              </form>
            </Form>
          )}

          {step === 1 && (
            <Form {...form}>
              <form className="grid gap-y-4" onSubmit={handleSubmit(onSubmit)}>
                {/* TODO: add more fields */}

                {renderActionBtns()}
              </form>
            </Form>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default ExerciseCreator;
