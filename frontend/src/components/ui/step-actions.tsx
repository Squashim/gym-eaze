import { ChevronLeft, ChevronRight, Loader2, X } from 'lucide-react';
import { Button } from './button';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';
import { useId, type Ref } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMediaQuery } from '@/hooks/use-media-query';

type StepActionsProps = {
  step: number;
  totalSteps: number;
  prevStep: () => void;
  nextStep: () => void;
  exitLabel: string;
  submitLabel: string;
  submittingLabel?: string;
  refs: {
    submitRef?: Ref<HTMLButtonElement>;
    cancelRef?: Ref<HTMLButtonElement>;
  };
  isSubmitting: boolean;
};

const StepActions = ({
  step,
  totalSteps,
  prevStep,
  nextStep,
  exitLabel,
  submitLabel,
  submittingLabel = 'Wysyłanie...',
  refs,
  isSubmitting,
}: StepActionsProps) => {
  const uid = useId();
  const isFirstStep = step === 0;
  const isLastStep = step === totalSteps - 1;
  const isBackAvailable = step < totalSteps - 1;
  const navigate = useNavigate();
  const isSmallBackIcon = useMediaQuery('(width <= 400px)');

  const handleCancel = () => {
    void navigate(-1);
  };

  return (
    <div className="flex justify-between items-center gap-4 md:col-span-2">
      {!isFirstStep ? (
        isSmallBackIcon ? (
          <Button
            className="size-10"
            onClick={prevStep}
            size="icon"
            type="button"
            variant="outline"
          >
            <ChevronLeft className="size-5" />
          </Button>
        ) : (
          <Button onClick={prevStep} size="lg" type="button" variant="outline">
            <ChevronLeft className="size-5" />
            Cofnij
          </Button>
        )
      ) : null}

      <div className="flex items-center ml-auto gap-3">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              aria-describedby={uid}
              className="size-10"
              onClick={handleCancel}
              ref={refs.cancelRef}
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

        {isBackAvailable && (
          <Button onClick={nextStep} size="lg" type="button">
            Kolejny etap
            <ChevronRight className="size-5" />
          </Button>
        )}

        {isLastStep && (
          <Button disabled={isSubmitting} ref={refs.submitRef} size="lg" type="submit">
            {isSubmitting ? (
              <>
                <Loader2 className="animate-spin" />
                {submittingLabel}
              </>
            ) : (
              submitLabel
            )}
          </Button>
        )}
      </div>
    </div>
  );
};

export default StepActions;
