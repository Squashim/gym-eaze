import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { IsPublicBadge } from '@/components/ui/item-info-badges';
import { Label } from '@/components/ui/label';
import Typography from '@/components/ui/typography';
import useOpen from '@/hooks/utils/use-open';
import { cn, getApiErrorMsg } from '@/lib/utils';
import { Info, Loader2 } from 'lucide-react';
import { useState, type PropsWithChildren } from 'react';

interface TabContentProps {
  title: string;
  description: string;
  isLoading: boolean;
  isError: boolean;
  isPublic?: boolean;
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
    <Card className="min-h-[40dvh]">
      <TabHeader
        description={description}
        isLoading={isLoading}
        isPublic={isPublic}
        title={title}
      />
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
  isLoading: boolean;
  isPublic?: boolean;
}

function TabHeader({ title, description, isPublic, isLoading }: TabHeaderProps) {
  return (
    <CardHeader>
      <CardTitle className="text-lg flex items-center gap-2 flex-wrap justify-between">
        {title}

        {isPublic !== undefined && <IsPublicBadge isLoading={isLoading} isPublic={isPublic} />}
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
    <Alert className="h-fit">
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
  className?: string;
}

function TabButtons({ onReset, disabled, isLoading, className }: TabButtonsProps) {
  return (
    <div
      className={cn(
        'flex items-center gap-2 justify-end mt-4 md:mt-2 col-span-full flex-wrap',
        className,
      )}
    >
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

interface RemoveAccountCardProps {
  username: string;
  isPending: boolean;
  onDelete: () => void;
}

function RemoveAccountCard({ username, onDelete, isPending }: RemoveAccountCardProps) {
  const deleteDialog = useOpen();
  const [confirmUsername, setConfirmUsername] = useState('');

  return (
    <>
      <Card className="border-destructive h-fit">
        <CardHeader>
          <CardTitle className="text-lg">Usuń konto</CardTitle>
          <CardDescription>
            Ta akcja jest nieodwracalna. Wszystkie Twoje dane zostaną trwale usunięte.
            <br />
            <strong>Uwaga:</strong> Jeśli udostępniłeś swoje plany treningowe lub ćwiczenia innym
            użytkownikom, nadal będą mieli do nich dostęp.
          </CardDescription>
        </CardHeader>

        <CardContent>
          <Button className="w-full" onClick={deleteDialog.open} variant="destructive">
            Usuń moje konto
          </Button>
        </CardContent>
      </Card>

      <Dialog onOpenChange={deleteDialog.setIsOpen} open={deleteDialog.isOpen}>
        <DialogContent className="[&>button]:hidden">
          <DialogHeader>
            <DialogTitle>Potwierdź usunięcie konta</DialogTitle>
            <DialogDescription>
              Czy na pewno chcesz usunąć swoje konto? Ta akcja jest nieodwracalna i spowoduje trwałe
              usunięcie wszystkich Twoich danych, w tym planów treningowych, ćwiczeń i postępów.
            </DialogDescription>
          </DialogHeader>

          <Label htmlFor="confirm-username">
            Aby potwierdzić wprowadź swoją nazwę: <Badge>{username}</Badge>
          </Label>

          <Input
            autoComplete="off"
            autoFocus
            id="confirm-username"
            maxLength={16}
            onChange={(e) => setConfirmUsername(e.target.value)}
            placeholder="Podaj swoją nazwę"
            type="text"
            value={confirmUsername}
          />

          <div className="flex items-center justify-end gap-2 mt-2">
            <Button
              disabled={isPending}
              onClick={deleteDialog.close}
              type="button"
              variant="outline"
            >
              Anuluj
            </Button>
            <Button
              className="w-38"
              disabled={confirmUsername !== username || isPending}
              onClick={onDelete}
              type="button"
              variant="destructive"
            >
              {isPending ? (
                <>
                  <Loader2 className="animate-spin" />
                  <span>Usuwanie...</span>
                </>
              ) : (
                <span>Usuń konto</span>
              )}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}

export { TabContent, TabDisclaimer, TabButtons, RemoveAccountCard };
