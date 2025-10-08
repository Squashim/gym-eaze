import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import Typography from '@/components/ui/typography';
import { getApiErrorMsg } from '@/lib/utils';
import { Globe2, Info, Loader2, Lock } from 'lucide-react';
import type { PropsWithChildren } from 'react';

interface TabContentProps {
  title: string;
  description: string;
  isLoading: boolean;
  isError: boolean;
  isPublic: boolean;
  error: Error | null;
}

function TabContent({
  title,
  description,
  isLoading,
  isPublic,
  isError,
  error,
  children,
}: PropsWithChildren<TabContentProps>) {
  return (
    <Card className="overflow-hidden flex flex-col w-full min-h-[40dvh]">
      <TabHeader description={description} isPublic={isPublic} title={title} />
      <CardContent className="w-full grid grid-cols-1 gap-4 h-full">
        {isLoading ? (
          <Loader2 className="m-auto animate-spin size-12" />
        ) : isError ? (
          <Typography className="text-center border-0 m-auto" variant="h4">
            {getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie później.'}
          </Typography>
        ) : (
          children
        )}
      </CardContent>
    </Card>
  );
}

interface TabHeaderProps {
  title: string;
  description: string;
  isPublic: boolean;
}

function TabHeader({ title, description, isPublic = false }: TabHeaderProps) {
  return (
    <CardHeader>
      <CardTitle className="text-lg flex items-center gap-2 flex-wrap justify-between">
        {title}
        <Tooltip>
          <TooltipTrigger asChild>
            <Badge className="size-7 rounded-full px-1 font-mono tabular-nums" tabIndex={0}>
              <div>{isPublic ? <Globe2 className="size-4" /> : <Lock className="size-4" />}</div>
            </Badge>
          </TooltipTrigger>
          <TooltipContent>
            {isPublic
              ? 'Te informacje są widoczne dla innych użytkowników'
              : 'Te informacje są widoczne tylko dla Ciebie'}
          </TooltipContent>
        </Tooltip>
      </CardTitle>
      <CardDescription>{description}</CardDescription>
    </CardHeader>
  );
}

interface TabDisclaimerProps {
  title: string;
  description: string;
}

function TabDisclaimer({ title, description }: TabDisclaimerProps) {
  return (
    <Alert>
      <Info />
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>{description}</AlertDescription>
    </Alert>
  );
}

interface TabButtonsProps {
  onReset?: () => void;
  disabled: boolean;
  isLoading: boolean;
}

function TabButtons({ onReset, disabled, isLoading }: TabButtonsProps) {
  return (
    <div className="flex items-center gap-2 justify-end mt-2">
      {onReset && (
        <Button disabled={isLoading || disabled} onClick={onReset} type="reset" variant="outline">
          Cofnij zmiany
        </Button>
      )}
      <Button className="w-40" disabled={disabled || isLoading} type="submit">
        {isLoading ? (
          <>
            <Loader2 className="animate-spin" />
            <span>Zapisywanie...</span>
          </>
        ) : (
          <span>Zapisz zmiany</span>
        )}
      </Button>
    </div>
  );
}

export { TabContent, TabDisclaimer, TabButtons };
