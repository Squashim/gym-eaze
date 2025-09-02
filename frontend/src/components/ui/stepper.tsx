import { cn } from '@/lib/utils';
import Typography from './typography';
import { Check } from 'lucide-react';

type StepperProps = {
  step: number;
  steps: string[];
};

const Stepper = ({ step, steps }: StepperProps) => {
  const totalSteps = steps.length;

  return (
    <div className="flex gap-2 justify-center">
      {steps.map((stepText, index) => (
        <div
          className={cn('flex items-start gap-2', index < totalSteps - 1 && 'basis-1/4')}
          key={index}
        >
          <div className="flex flex-col items-center gap-1 text-center p-1">
            <div
              className={cn(
                'size-10 rounded-full transition-all duration-400 flex items-center justify-center shadow-xs border border-muted dark:border-border bg-input/30',
                index === step && 'border-primary dark:border-primary w-16 sm:w-24',
                index === totalSteps - 1 &&
                  index === step &&
                  'bg-secondary border-primary dark:border-border text-secondary-foreground',
                index < step && 'bg-secondary dark:border-border text-secondary-foreground',
              )}
            >
              {step === totalSteps - 1 && index === step ? (
                <Check className="size-5" />
              ) : (
                <Typography className="font-semibold" variant="small">
                  {index + 1}
                </Typography>
              )}
            </div>
            <Typography variant="small">{stepText}</Typography>
          </div>
          {index < totalSteps - 1 && (
            <div className="bg-ring rounded-xs w-full h-0.5 translate-y-5.5" />
          )}
        </div>
      ))}
    </div>
  );
};

export default Stepper;
