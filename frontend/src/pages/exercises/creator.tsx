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
import { useStepForm } from '@/hooks/use-step-form';
import Stepper from '@/components/ui/stepper';
import StepActions from '@/components/ui/step-actions';
import { Textarea } from '@/components/ui/textarea';
import { MultiSelect } from '@/components/ui/multi-select';
import { TagsInput } from '@/components/ui/tags-input';
import { apiResponseToMultiSelectOptions, cn, extractDirtyFieldsData } from '@/lib/utils';
import { Badge } from '@/components/ui/badge';
import { AdvancedLevel } from '@/constants/advanced-level';
import useGetEquipment from '@/hooks/query/equipment/use-get-equipment';
import useGetMuscleGroups from '@/hooks/query/muscle-groups/use-get-muscle-groups';
import type { MuscleGroup } from '@/types/muscle-group';
import type { Equipment } from '@/types/equipment';
import { useEffect, useMemo, type PropsWithChildren } from 'react';
import { AdvancedLevelBadge, VisibilityBadge } from '@/components/ui/item-info-badges';
import { exerciseCreatorConstants } from '@/constants/forms';
import { useExercise } from '@/hooks/query/exercises/use-exercise';
import { useUser } from '@/context/user';
import type { CreateExercisePayload, UpdateExercisePayload } from '@/types/exercise';
import { useParams } from 'react-router-dom';
import useGetExercise from '@/hooks/query/exercises/use-get-exercise';
import { Loader2 } from 'lucide-react';
import { Visibility } from '@/constants/visibility';
import { useSequentialGuards } from '@/guards/use-sequential-guards';
import useFormFiles from '@/hooks/use-form-files';
import usePageTitle from '@/hooks/use-page-title';
import PageLayout from '@/components/layout/page';

type ExerciseCreatorProps = {
  mode: 'create' | 'edit';
};


const ExerciseCreator = ({ mode }: ExerciseCreatorProps) => {
  const { form, step, nextStep, prevStep, totalSteps, refs, handleKeyDown } = useStepForm({
    schema: exerciseSchema,
    defaultValues: exerciseCreatorConstants.defaultValues,
    totalSteps: exerciseCreatorConstants.steps.length,
  });

  const { createExercise, updateExercise } = useExercise();
  const { user } = useUser();
  const { handleSubmit } = form;

  const isEditMode = mode === 'edit';
  const { id } = useParams();
  const {
    data: exerciseData,
    isLoading,
    isError,
  } = useGetExercise(id!, {
    enabled: isEditMode && Boolean(id),
  });

  const guards = useMemo(
    () => [
      {
        check: () => {
          if (!isEditMode) return true;
          if (isLoading) return 'wait';
          return !isError && !!exerciseData;
        },
        redirectTo: '/exercises/browse',
        errorMessage: 'Identyfikator ćwiczenia jest niepoprawny.',
      },
      {
        check: () => {
          if (!isEditMode) return true;
          if (isLoading) return 'wait';
          return exerciseData?.author._id === user?._id;
        },
        redirectTo: `/exercises/${exerciseData?._id}`,
        errorMessage: 'Nie masz uprawnień do edytowania tego ćwiczenia.',
      },
    ],
    [isEditMode, isLoading, isError, exerciseData, user],
  );
  const { passed: guardsPassed, ready: guardsReady } = useSequentialGuards(guards);
  const {
    imageFile: defaultImageFile,
    videoFile: defaultVideoFile,
    isLoading: isFilesLoading,
    compareFiles,
  } = useFormFiles({
    imageUrl: exerciseData?.image,
    videoUrl: exerciseData?.video,
    enabled: isEditMode && Boolean(exerciseData),
  });

  usePageTitle(isEditMode ? 'Edytor ćwiczenia' : 'Kreator ćwiczenia');

  useEffect(() => {
    if (!guardsPassed || !exerciseData || !defaultImageFile || !defaultVideoFile || isError) return;

    const fillForm = () => {
      form.reset({
        stepOne: {
          name: exerciseData.name,
          image: defaultImageFile,
          video: defaultVideoFile,
          advancedLevel: exerciseData.advancedLevel,
          visibility: exerciseData.visibility,
        },
        stepTwo: {
          description: exerciseData.description,
          muscleGroup: exerciseData.muscleGroup.map((mg) => mg._id),
          equipment: exerciseData.equipment.map((eq) => eq._id),
          hashtags: exerciseData.hashtags || [],
        },
      });
    };

    void fillForm();
  }, [guardsPassed, exerciseData, defaultImageFile, defaultVideoFile, isError, form]);

  const imageFile = form.watch('stepOne.image');
  const videoFile = form.watch('stepOne.video');

  const handleCreate = (formData: ExerciseSchema) => {
    const formattedData = {
      ...formData.stepOne,
      ...formData.stepTwo,
    } as CreateExercisePayload;

    createExercise.mutate(formattedData);
  };

  const fileComparison = useMemo(() => {
    return compareFiles(imageFile, videoFile);
  }, [imageFile, videoFile, compareFiles]);

  const { isImageDefault, isVideoDefault } = fileComparison;

  const handleUpdate = (formData: ExerciseSchema) => {
    const dirtyFields = form.formState.dirtyFields;

    const formattedData = extractDirtyFieldsData<ExerciseSchema, UpdateExercisePayload>(
      dirtyFields,
      formData,
    );

    if (!isImageDefault) {
      formattedData.image = formData.stepOne.image;
    }

    if (!isVideoDefault) {
      formattedData.video = formData.stepOne.video;
    }

    updateExercise.mutate({ id: id!, payload: formattedData });
  };

  const onSubmit = (formData: ExerciseSchema) => {
    if (isEditMode) {
      handleUpdate(formData);
    } else {
      handleCreate(formData);
    }
  };

  return (
    <PageLayout
      className="max-w-lg md:max-w-3xl mx-auto"
      title={isEditMode ? 'Edytor ćwiczenia' : 'Kreator ćwiczenia'}
      titleClassName="text-center"
    >
      <Stepper step={step} steps={exerciseCreatorConstants.steps} />
      <Card className="overflow-hidden min-h-[60dvh] flex flex-col items-center justify-center">
        <CardContent className="w-full">
          {isEditMode && (isLoading || isFilesLoading || !guardsReady || !guardsPassed) ? (
            <>
              <Loader2 className="mx-auto mb-4 animate-spin" size={32} />
              <Typography className="text-center" variant="p">
                Ładowanie danych ćwiczenia...
              </Typography>
            </>
          ) : (
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
                  exitLabel={isEditMode ? 'Wyjdź z edytora' : 'Wyjdź z kreatora'}
                  isDirty={form.formState.isDirty || !isImageDefault || !isVideoDefault}
                  isSubmitting={createExercise.isPending || updateExercise.isPending}
                  nextStep={nextStep}
                  prevStep={prevStep}
                  refs={refs}
                  step={step}
                  submitDisabled={
                    isEditMode && !form.formState.isDirty && isImageDefault && isVideoDefault
                  }
                  submitLabel={isEditMode ? 'Edytuj ćwiczenie' : 'Stwórz ćwiczenie'}
                  submittingLabel={isEditMode ? 'Edytowanie...' : 'Tworzenie...'}
                  totalSteps={totalSteps}
                />
              </form>
            </Form>
          )}
        </CardContent>
      </Card>
    </PageLayout>
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
            <FormDescription>Maksymalna długość nazwy to 150 znaków.</FormDescription>
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
          <FormItem className="flex flex-row items-center justify-between rounded-md border p-3">
            <div className="flex flex-col gap-1">
              <FormLabel className="font-semibold">Publiczne ćwiczenie*</FormLabel>
              <FormDescription>
                Inni użytkownicy będą mogli wyszukać i korzystać z tego ćwiczenia.
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
    </>
  );
};

const ExerciseCreatorStepTwo = () => {
  const { control } = useFormContext<ExerciseSchema>();
  const { data: equipment, isLoading: isEquipmentLoading } = useGetEquipment();
  const { data: muscleGroups, isLoading: isMuscleGroupsLoading } = useGetMuscleGroups();

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
                className="resize-none min-h-48 max-h-48 scrollbar overflow-y-auto"
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
                defaultValue={isMuscleGroupsLoading ? [] : field.value}
                disabled={isMuscleGroupsLoading}
                maxCount={3}
                onValueChange={field.onChange}
                options={apiResponseToMultiSelectOptions(muscleGroups)}
                placeholder={isMuscleGroupsLoading ? 'Ładowanie...' : 'Wybierz grupy mięśniowe...'}
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
                defaultValue={isEquipmentLoading ? [] : field.value}
                disabled={isEquipmentLoading}
                hideSelectAll
                maxCount={3}
                onValueChange={field.onChange}
                options={apiResponseToMultiSelectOptions(equipment)}
                placeholder={isEquipmentLoading ? 'Ładowanie...' : 'Wybierz sprzęt...'}
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
              Hasztag może zawierać tylko małe litery i cyfry. Możesz dodać do 50 hasztagów.
            </FormDescription>
          </FormItem>
        )}
      />
    </>
  );
};

const ExerciseCreatorStepThree = () => {
  const { watch } = useFormContext<ExerciseSchema>();
  const { data: equipment } = useGetEquipment();
  const { data: muscleGroups } = useGetMuscleGroups();
  const formData = watch();

  const muscleMap = useMemo(() => {
    const map: Record<string, MuscleGroup> = {};
    if (!muscleGroups) return map;

    muscleGroups.forEach((muscle) => {
      map[muscle._id] = muscle;
    });
    return map;
  }, [muscleGroups]);

  const equipmentMap = useMemo(() => {
    const map: Record<string, Equipment> = {};
    if (!equipment) return map;

    equipment.forEach((eq) => {
      map[eq._id] = eq;
    });
    return map;
  }, [equipment]);

  const InfoField = ({
    label,
    fullWidth = false,
    children,
  }: PropsWithChildren & {
    fullWidth?: boolean;
    label: string;
  }) => (
    <div className={cn('flex flex-col gap-1', fullWidth && 'md:col-span-2')}>
      <Typography className="font-semibold" variant="small">
        {label}:
      </Typography>
      {children}
    </div>
  );

  const ItemsHorizontalGallery = <T extends { _id: string; name: string; image: string }>({
    itemIds,
    itemMap,
    isRounded = false,
  }: {
    itemIds: string[];
    itemMap: Record<string, T>;
    isRounded?: boolean;
  }) => {
    return (
      <div className="flex gap-2 items-center overflow-x-auto scrollbar py-1">
        {itemIds.map((id) => {
          const item = itemMap[id];
          if (!item) return null;

          return (
            <Badge
              className="font-semibold max-w-38 break-words whitespace-break-spaces text-center bg-muted-foreground/20"
              key={item._id}
              variant="outline"
            >
              <img
                alt={`zdjęcie przedstawiające ${item.name}`}
                className={cn('size-13 aspect-square object-cover', isRounded && 'rounded-lg')}
                src={item.image}
              />
              {item.name}
            </Badge>
          );
        })}
      </div>
    );
  };

  return (
    <>
      <div className="md:col-span-2">
        <Typography className="md:col-span-2" variant="h3">
          Sprawdź poprawność danych
        </Typography>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
          <InfoField fullWidth label="Nazwa ćwiczenia">
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

          <InfoField label="Widoczność ćwiczenia">
            <VisibilityBadge type="Exercise" visibility={formData.stepOne.visibility} />
          </InfoField>

          <InfoField fullWidth label="Opis ćwiczenia">
            <Typography
              className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent-foreground/10"
              variant="p"
            >
              {formData.stepTwo.description}
            </Typography>
          </InfoField>

          <InfoField fullWidth label="Grupy mięśniowe">
            <ItemsHorizontalGallery itemIds={formData.stepTwo.muscleGroup} itemMap={muscleMap} />
          </InfoField>

          <InfoField fullWidth label="Sprzęt ćwiczeniowy">
            <ItemsHorizontalGallery
              isRounded
              itemIds={formData.stepTwo.equipment}
              itemMap={equipmentMap}
            />
          </InfoField>

          {formData.stepTwo.hashtags.length > 0 && (
            <InfoField fullWidth label="Hasztagi">
              <div className="flex flex-wrap gap-1 max-h-16 overflow-y-auto scrollbar">
                {formData.stepTwo.hashtags.map((hashtag) => {
                  return (
                    <Badge key={hashtag} variant="outline">
                      {`#${hashtag}`}
                    </Badge>
                  );
                })}
              </div>
            </InfoField>
          )}
        </div>
      </div>
    </>
  );
};
