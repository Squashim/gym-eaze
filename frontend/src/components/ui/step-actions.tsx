import { ChevronLeft, ChevronRight, Loader2, X } from 'lucide-react';
import { Button } from './button';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';
import { useId, type Ref } from 'react';
import { useMediaQuery } from '@/hooks/utils/use-media-query';
import { useUnsavedChangesWarning } from '@/hooks/utils/use-unsaved-changes-warning';
import { cn } from '@/lib/utils';

type StepActionsProps = {
  step: number;
  totalSteps: number;
  prevStep: () => void;
  nextStep: () => void;
  exitLabel: string;
  submitLabel: string;
  submittingLabel?: string;
  isSubmitting: boolean;
  submitDisabled?: boolean;
  isDirty?: boolean;
  fixed?: boolean;
  refs: {
    submitRef?: Ref<HTMLButtonElement>;
    cancelRef?: Ref<HTMLButtonElement>;
  };
};

const StepActions = ({
  step,
  totalSteps,
  prevStep,
  nextStep,
  exitLabel,
  submitLabel,
  submittingLabel = 'Wysyłanie...',
  submitDisabled = false,
  isDirty = false,
  refs,
  fixed = false,
  isSubmitting,
}: StepActionsProps) => {
  const uid = useId();
  const { handleCancel } = useUnsavedChangesWarning(isDirty);
  const isFirstStep = step === 0;
  const isLastStep = step === totalSteps - 1;
  const isBackAvailable = step < totalSteps - 1;
  const isSmallBackIcon = useMediaQuery('(width <= 400px)');

  return (
    <div
      className={cn(
        'flex justify-between items-center gap-4 md:col-span-full',
        fixed &&
          'md:max-w-3xl max-w-lg mx-auto fixed bottom-0 right-0 left-0 inline-flex gap-2 px-7 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 rounded-t-lg shadow-lg h-navbar',
      )}
    >
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
          <Button
            disabled={isSubmitting || submitDisabled}
            ref={refs.submitRef}
            size="lg"
            type="submit"
          >
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
