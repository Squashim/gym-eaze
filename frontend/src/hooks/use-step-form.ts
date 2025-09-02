import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useRef, useState, type KeyboardEvent } from 'react';
import { useForm, type DefaultValues, type Path } from 'react-hook-form';
import type { z, ZodTypeAny } from 'zod/v3';

interface UseStepFormProps<TSchema extends ZodTypeAny> {
  schema: TSchema;
  defaultValues: DefaultValues<z.infer<TSchema>>;
  totalSteps: number;
}

export function UseStepForm<TSchema extends ZodTypeAny>({
  schema,
  defaultValues,
  totalSteps,
}: UseStepFormProps<TSchema>) {
  type FormValues = z.infer<TSchema>;

  const form = useForm<FormValues>({
    shouldUnregister: false,
    resolver: zodResolver(schema),
    mode: 'onChange',
    defaultValues,
  });
  const [step, setStep] = useState(0);
  const submitRef = useRef<HTMLButtonElement>(null);
  const cancelRef = useRef<HTMLButtonElement>(null);
  const stepKeys = Object.keys(defaultValues) as Array<keyof FormValues>;

  useEffect(() => {
    if (step === totalSteps - 1) {
      submitRef.current?.focus();
    }
  }, [step, totalSteps]);

  const validateStep = async () => {
    const stepKey = stepKeys[step];
    return form.trigger(stepKey as Path<FormValues>);
  };

  const nextStep = async () => {
    const isValid = await validateStep();
    if (!isValid) return;

    if (step < totalSteps - 1) {
      setStep((prev) => prev + 1);
    }
  };

  const prevStep = () => {
    if (step > 0) {
      setStep((prev) => prev - 1);
    }
  };

  const handleKeyDown = async (e: KeyboardEvent<HTMLElement>) => {
    const target = e.target as HTMLElement;

    if (
      e.key === 'Escape' &&
      !(!(target instanceof HTMLButtonElement) && target.role === 'combobox')
    ) {
      e.preventDefault();
      cancelRef.current?.focus();
    }

    if (
      e.key === 'Enter' &&
      step < totalSteps - 1 &&
      !(target instanceof HTMLTextAreaElement) &&
      !(target instanceof HTMLDivElement && target.dataset['type'] === 'file') &&
      !(target instanceof HTMLInputElement && target.dataset['type'] === 'hashtag-input') &&
      !(target.role === 'combobox')
    ) {
      e.preventDefault();
      await nextStep();
    }
  };

  return {
    form,
    step,
    totalSteps,
    refs: {
      submitRef,
      cancelRef,
    },
    nextStep,
    prevStep,
    handleKeyDown,
  };
}
