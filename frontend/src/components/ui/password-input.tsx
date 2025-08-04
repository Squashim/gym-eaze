import {
  useState,
  useMemo,
  type Dispatch,
  type SetStateAction,
  type ComponentPropsWithRef,
  useRef,
} from 'react';
import { Input } from './input';
import { cn } from '@/lib/utils';
import { Button } from './button';
import { CheckIcon, EyeIcon, EyeOffIcon, XIcon } from 'lucide-react';
import { Popover, PopoverContent, PopoverTrigger } from './popover';

type PasswordInputProps = ComponentPropsWithRef<'input'> & {
  showRequirements?: boolean;
};

const PasswordInput = ({
  className,
  value,
  showRequirements = false,
  ...props
}: PasswordInputProps) => {
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);
  const disabled = value === '' || value === undefined || props.disabled;

  const checkStrength = (pass: string) => {
    const requirements = [
      { regex: /.{14,}/, text: 'Co najmniej 14 znaków' },
      { regex: /[0-9]/, text: 'Co najmniej 1 cyfra' },
      { regex: /[a-z]/, text: 'Co najmniej 1 mała litera' },
      { regex: /[A-Z]/, text: 'Co najmniej 1 duża litera' },
      { regex: /[$&+,:;=?@#|'<>.^*()%!-]/, text: 'Co najmniej 1 znak specjalny' },
    ];

    return requirements.map((req) => ({
      met: req.regex.test(pass),
      text: req.text,
    }));
  };

  const strength = checkStrength(value?.toString() ?? '');
  const strengthScore = useMemo(() => {
    return strength.filter((req) => req.met).length;
  }, [strength]);

  const getStrengthColor = (score: number) => {
    if (score === 0) return 'bg-border';
    if (score <= 1) return 'bg-red-500';
    if (score <= 2) return 'bg-orange-500';
    if (score <= 3) return 'bg-amber-500';
    if (score == 4) return 'bg-yellow-500';
    return 'bg-secondary';
  };

  const getStrengthText = (score: number) => {
    if (score === 0) return 'Wprowadź hasło';
    if (score <= 2) return 'Słabe hasło';
    if (score <= 3) return 'Średnie hasło';
    if (score === 4) return 'Mocne hasło';
    return 'Bardzo mocne hasło';
  };

  const handleBlurCapture = () => {
    requestAnimationFrame(() => {
      if (wrapperRef.current && !wrapperRef.current.contains(document.activeElement)) {
        setIsPopoverOpen(false);
      }
    });
  };

  if (!showRequirements) {
    return (
      <div className="relative">
        <Input
          className={cn('hide-password-toggle pr-10', className)}
          type={showPassword ? 'text' : 'password'}
          {...props}
        />
        <ShowPasswordToggle
          disabled={disabled}
          setShowPassword={setShowPassword}
          showPassword={showPassword}
        />
      </div>
    );
  }

  return (
    <Popover onOpenChange={setIsPopoverOpen} open={isPopoverOpen}>
      <PopoverTrigger asChild>
        <div className="relative" onBlurCapture={handleBlurCapture} ref={wrapperRef}>
          <Input
            className={cn('hide-password-toggle pr-10', className)}
            onClick={(e) => {
              setIsPopoverOpen(true);
              e.preventDefault();
            }}
            onFocus={(e) => {
              setIsPopoverOpen(true);
              e.preventDefault();
            }}
            type={showPassword ? 'text' : 'password'}
            {...props}
            aria-describedby="password-requirements"
          />
          <ShowPasswordToggle
            disabled={disabled}
            setShowPassword={setShowPassword}
            showPassword={showPassword}
          />
        </div>
      </PopoverTrigger>

      <PopoverContent
        align="start"
        className={cn(
          'w-[var(--radix-popover-trigger-width)]',
          props['aria-invalid'] ? 'mt-8' : 'mt-0.5',
        )}
        id="password-requirements"
        onEscapeKeyDown={(e) => e.preventDefault()}
        onOpenAutoFocus={(e) => e.preventDefault()}
      >
        <div
          aria-label="Siła hasła"
          aria-valuemax={5}
          aria-valuemin={0}
          aria-valuenow={strengthScore}
          className="bg-border mt-3 mb-4 h-1 w-full overflow-hidden rounded-full"
          role="progressbar"
        >
          <div
            className={`h-full ${getStrengthColor(strengthScore)} transition-all duration-500 ease-out`}
            style={{ width: `${(strengthScore / 5) * 100}%` }}
          ></div>
        </div>
        <p className="text-foreground mb-2 text-sm font-medium">
          <b>{getStrengthText(strengthScore)}</b>. Musi spełniać wszystkie wymagania.
        </p>
        <ul aria-label="Wymagania hasła" className="space-y-1.5">
          {strength.map((req, index) => (
            <li className="flex items-center gap-2" key={index}>
              {req.met ? (
                <CheckIcon aria-hidden="true" className="text-secondary" size={16} />
              ) : (
                <XIcon aria-hidden="true" className="text-muted-foreground/80" size={16} />
              )}
              <span className={`text-xs ${req.met ? 'text-secondary' : 'text-muted-foreground'}`}>
                {req.text}
                <span className="sr-only">
                  {req.met ? ' - Spełniono wymagania' : ' - Nie spełniono wymagań'}
                </span>
              </span>
            </li>
          ))}
        </ul>
      </PopoverContent>
    </Popover>
  );
};

type ShowPasswordToggleProps = {
  disabled?: boolean;
  showPassword: boolean;
  setShowPassword: Dispatch<SetStateAction<boolean>>;
};

const ShowPasswordToggle = ({
  disabled,
  showPassword,
  setShowPassword,
}: ShowPasswordToggleProps) => {
  return (
    <Button
      className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
      disabled={disabled}
      onClick={(e) => {
        e.stopPropagation();
        setShowPassword((prev) => !prev);
      }}
      size="sm"
      type="button"
      variant="ghost"
    >
      {showPassword && !disabled ? (
        <EyeIcon aria-hidden="true" className="h-4 w-4" />
      ) : (
        <EyeOffIcon aria-hidden="true" className="h-4 w-4" />
      )}
      <span className="sr-only">{showPassword ? 'Ukryj hasło' : 'Pokaż hasło'}</span>
    </Button>
  );
};

export { PasswordInput };
