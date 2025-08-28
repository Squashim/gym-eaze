import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
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
  const stepKeys = Object.keys(defaultValues) as Array<keyof FormValues>;

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

  return {
    form,
    step,
    nextStep,
    prevStep,
    totalSteps,
  };
}
