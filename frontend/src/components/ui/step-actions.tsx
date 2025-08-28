import { ChevronLeft, ChevronRight, X } from 'lucide-react';
import { Button } from './button';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';
import { useId } from 'react';

type StepActionsProps = {
  step: number;
  totalSteps: number;
  prevStep: () => void;
  nextStep: () => void;
  exitLabel: string;
  submitLabel: string;
};

const StepActions = ({
  step,
  totalSteps,
  prevStep,
  nextStep,
  exitLabel,
  submitLabel,
}: StepActionsProps) => {
  const uid = useId();

  return (
    <div className="flex justify-between items-center gap-4 md:col-span-2">
      {step !== 0 && (
        <Button onClick={prevStep} size="lg" type="button" variant="outline">
          <ChevronLeft className="size-5" />
          Cofnij
        </Button>
      )}
      <div className="flex items-center ml-auto gap-3">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              aria-describedby={uid}
              className="size-10"
              size="icon"
              type="button"
              variant="outline"
            >
              <X className="size-5" />
            </Button>
          </TooltipTrigger>
          <TooltipContent id={uid}>
            <Typography variant="small">{exitLabel}</Typography>
          </TooltipContent>
        </Tooltip>

        {step < totalSteps - 1 && (
          <Button onClick={nextStep} size="lg" type="button">
            Kolejny etap
            <ChevronRight className="size-5" />
          </Button>
        )}

        {step === totalSteps - 1 && (
          <Button size="lg" type="submit">
            {submitLabel}
          </Button>
        )}
      </div>
    </div>
  );
};

export default StepActions;
