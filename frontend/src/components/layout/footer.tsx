import { Separator } from '../ui/separator';
import Typography from '../ui/typography';
import { Button } from '../ui/button';
import { ExternalLink, Mail, X } from 'lucide-react';
import Logo from '../ui/logo';
import useOpen from '@/hooks/utils/use-open';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '../ui/dialog';
import { Badge } from '../ui/badge';
import useGetEquipment from '@/hooks/query/equipment/use-get-equipment';
import type { PropsWithChildren } from 'react';
import { Skeleton } from '../ui/skeleton';

function Footer() {
  const usedMediaDialog = useOpen();
  const termsDialog = useOpen();
  const authorsDialog = useOpen();

  return (
    <>
      <footer className="w-full bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 rounded-lg shadow-lg">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 p-6">
          <div className="flex flex-col gap-4 lg:col-span-1">
            <div className="flex items-center gap-3">
              <Logo className="size-16 min-w-16" />
              <div className="flex flex-col">
                <Typography className="font-semibold" variant="h4">
                  GymEaze
                </Typography>
                <Typography className="text-muted-foreground" variant="small">
                  Trenuj z nami
                </Typography>
              </div>
            </div>
            <Typography className="text-balance max-w-sm" variant="muted">
              GymEaze to serwis społecznościowy stworzona dla użytkowników zainteresowanych
              aktywnością fizyczną i zdrowym stylem życia.
            </Typography>
          </div>

          <div className="flex flex-col gap-4">
            <Typography className="font-semibold" variant="h4">
              Ważne informacje
            </Typography>
            <div className="flex flex-col gap-2">
              <Button
                className="text-muted-foreground px-0 justify-start"
                onClick={usedMediaDialog.open}
                variant="link"
              >
                Wykorzystane multimedia
              </Button>
              <Button
                className="text-muted-foreground px-0 justify-start"
                onClick={termsDialog.open}
                variant="link"
              >
                Regulamin korzystania z serwisu
              </Button>
              <Button
                className="text-muted-foreground px-0 justify-start"
                onClick={authorsDialog.open}
                variant="link"
              >
                Autorzy i technologie
              </Button>
            </div>
          </div>

          <div className="flex flex-col gap-4">
            <Typography className="font-semibold" variant="h4">
              Wsparcie i błędy
            </Typography>
            <div className="flex flex-col gap-3">
              <Typography className="text-sm leading-relaxed" variant="muted">
                Jeśli masz pytania, potrzebujesz pomocy lub znalazłeś błąd podczas korzystania z
                serwisu, skontaktuj się z naszym wsparciem pod adresem:
              </Typography>
              <a
                className="flex items-center gap-2 hover:underline text-sm text-primary transition-colors"
                href="#"
              >
                <Mail className="size-4" />
                gymeaze.pomoc@gmail.com
              </a>
            </div>
          </div>
        </div>

        <Separator />

        <div className="flex items-center justify-between flex-wrap gap-4 p-4">
          <Typography className="text-sm" variant="muted">
            2025 GymEaze.
          </Typography>
          <Typography className="text-xs" variant="muted">
            Wersja 1.0.0
          </Typography>
        </div>
      </footer>

      <UsedMediaDialog
        onClose={usedMediaDialog.close}
        onOpenChange={usedMediaDialog.setIsOpen}
        open={usedMediaDialog.isOpen}
      />
      <TermsDialog
        onClose={termsDialog.close}
        onOpenChange={termsDialog.setIsOpen}
        open={termsDialog.isOpen}
      />

      <AuthorsDialog
        onClose={authorsDialog.close}
        onOpenChange={authorsDialog.setIsOpen}
        open={authorsDialog.isOpen}
      />
    </>
  );
}

interface FooterDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

function UsedMediaDialog({ open, onOpenChange, onClose }: FooterDialogProps) {
  const { data, isLoading, isError } = useGetEquipment({
    enabled: open,
  });

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden p-0 gap-0 sm:max-w-2xl flex flex-col max-h-[95vh] h-[95vh]">
        <FooterDialogHeader onClose={onClose} title="Wykorzystane multimedia" />
        <Separator />
        <div className="flex-1 min-h-0 p-4 overflow-y-auto scrollbar space-y-2">
          <FooterMediaLinkGroup title="Opracowanie własne">
            <li>
              <Typography className="text-sm">Zdjęcia grup mięśniowych</Typography>
            </li>
            <li>
              <Typography className="text-sm">Zdjęcia i filmy ćwiczeń</Typography>
            </li>
            <li>
              <Typography className="text-sm">Zdjęcia planów treningowych</Typography>
            </li>
            <li>
              <Typography className="text-sm">Zdjęcia odznak</Typography>
            </li>
          </FooterMediaLinkGroup>

          <Separator />

          <FooterMediaLinkGroup title="Zdjęcia na stronie logowania">
            <FooterMediaLink to="https://unsplash.com/photos/a-man-is-doing-exercises-with-dumbs-in-a-gym-t9bG_FOXqMA" />
          </FooterMediaLinkGroup>

          <FooterMediaLinkGroup title="Zdjęcia na stronie rejestracji">
            <FooterMediaLink to="https://unsplash.com/photos/man-sitting-down-while-tying-shoe-lace-7RUvirkSMOY?utm_content=creditShareLink&utm_medium=referral&utm_source=unsplash" />
          </FooterMediaLinkGroup>

          <FooterMediaLinkGroup title="Ikony">
            <FooterMediaLink to="https://lucide.dev/icons/" />
          </FooterMediaLinkGroup>

          <FooterMediaLinkGroup title="Zdjęcia sprzętu treningowego">
            {isLoading && <Skeleton className="w-1/3 h-6" />}
            {(isError || !data) && (
              <Typography className="text-destructive">Błąd podczas ładowania danych.</Typography>
            )}
            {data?.map((eq) => (
              <FooterMediaLink key={eq._id} to={eq.imageCredit} />
            ))}
          </FooterMediaLinkGroup>
        </div>
      </DialogContent>
    </Dialog>
  );
}

function TermsDialog({ open, onOpenChange, onClose }: FooterDialogProps) {
  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden p-0 gap-0 sm:max-w-2xl flex flex-col max-h-[95vh] h-[95vh]">
        <FooterDialogHeader onClose={onClose} title="Regulamin korzystania z serwisu" />
        <Separator />
        <div className="flex-1 min-h-0 p-4 overflow-y-auto scrollbar">
          <ul className="list-decimal ml-4 space-y-2">
            <li>
              <Typography className="font-semibold">Postanowienia ogólne</Typography>
              <ol className="list-disc ml-4">
                <li>
                  Wszystkie określone poniżej zasady i warunki nie mają charakteru wiążącej umowy.
                </li>
                <li>
                  Podpunkty mają na celu zapełnienie treści strony i nie stanowią faktycznych
                  postanowień regulaminu.
                </li>
                <li>
                  Celem regulaminu jest stworzenie wrażenia profesjonalizmu i kompleksowości
                  serwisu.
                </li>
              </ol>
            </li>
            <li>
              <Typography className="font-semibold">Rejestracja i dostęp</Typography>
              <ol className="list-disc ml-4">
                <li>Korzystanie z serwisu jest dobrowolne i bezpłatne.</li>
                <li>
                  Dane podawane podczas rejestracji nie muszą być prawdziwe. Użytkownik odpowiada za
                  bezpieczeństwo własnego hasła.
                </li>
                <li>
                  W ramach testów dostępne jest logowanie za pomocą konta Google, po wcześniejszej
                  konfiguracji odpowiednich zmiennych środowiskowych.
                </li>
                <li>
                  Administrator może w dowolnym momencie usunąć konto lub ograniczyć dostęp w ramach
                  testowania.
                </li>
              </ol>
            </li>

            <li>
              <Typography className="font-semibold">Zasady korzystania</Typography>
              <ol className="list-disc ml-4">
                <li>
                  Serwis służy do społecznościowych interakcji związanych z aktywnością fizyczną.
                </li>
                <li>
                  Zabrania się m.in.:
                  <ul className="list-disc ml-4">
                    <li>publikowania treści niezgodnych z prawem, wulgarnych lub obraźliwych,</li>
                    <li>
                      promowania dopingu, substancji niedozwolonych lub niebezpiecznych praktyk
                      treningowych,
                    </li>
                    <li>spamowania lub umieszczania reklam bez zgody Administratora,</li>
                    <li>podszywania się pod inne osoby.</li>
                  </ul>
                </li>
                <li>Użytkownicy powinni zachowywać kulturę wypowiedzi i wzajemny szacunek.</li>
              </ol>
            </li>

            <li>
              <Typography className="font-semibold">
                Treści publikowane przez użytkowników
              </Typography>
              <ol className="list-disc ml-4">
                <li>
                  Użytkownik ponosi odpowiedzialność za treści, które publikuje (posty, zdjęcia,
                  komentarze).
                </li>
                <li>
                  Publikując materiały, użytkownik oświadcza, że posiada do nich prawa lub zgodę
                  osób przedstawionych na materiałach.
                </li>
                <li>Administrator może usuwać treści naruszające regulamin.</li>
                <li>
                  Wszystkie treści opublikowane są wyłącznie do celów edukacyjnych i projektowych.
                </li>
              </ol>
            </li>
          </ul>
        </div>
      </DialogContent>
    </Dialog>
  );
}

function AuthorsDialog({ open, onOpenChange, onClose }: FooterDialogProps) {
  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden p-0 gap-0 sm:max-w-2xl flex flex-col">
        <FooterDialogHeader
          description="Szczegóły o twórcach i technologiach projektu"
          onClose={onClose}
          title="Autorzy i technologie"
        />

        <Separator />

        <div className="flex-1 min-h-0 p-4 overflow-y-auto scrollbar">
          <ul className="list-disc ml-4">
            <li className="space-y-2 mb-4">
              <Typography variant="h4">Aplikacja internetowa</Typography>
              <div className="flex gap-2 items-center ml-4">
                <Typography className="font-semibold">Konrad</Typography>
                <Typography className="italic" variant="muted">
                  Squashim
                </Typography>
                <Button asChild className="p-0 text-muted-foreground ml-4" variant="link">
                  <a href="https://github.com/Squashim" rel="noreferrer" target="_blank">
                    <ExternalLink size="4" />
                    Zobacz profil GitHub
                  </a>
                </Button>
              </div>

              <div className="ml-4 flex gap-2 items-center">
                <Badge>React</Badge>
                <Badge>TypeScript</Badge>
                <Badge>Tailwind</Badge>
              </div>
            </li>

            <li className="space-y-2 mb-4">
              <Typography variant="h4">Aplikacja serwerowa</Typography>
              <div className="flex gap-2 items-center ml-4">
                <Typography className="font-semibold">Ewelina</Typography>
                <Typography className="italic" variant="muted">
                  evee03
                </Typography>
                <Button asChild className="p-0 text-muted-foreground ml-4" variant="link">
                  <a href="https://github.com/evee03" rel="noreferrer" target="_blank">
                    <ExternalLink size="4" />
                    Zobacz profil GitHub
                  </a>
                </Button>
              </div>

              <div className="flex gap-2 items-center ml-4">
                <Typography className="font-semibold">Konrad</Typography>
                <Typography className="italic" variant="muted">
                  Squashim
                </Typography>
                <Button asChild className="p-0 text-muted-foreground ml-4" variant="link">
                  <a href="https://github.com/Squashim" rel="noreferrer" target="_blank">
                    <ExternalLink size="4" />
                    Zobacz profil GitHub
                  </a>
                </Button>
              </div>

              <div className="ml-4 flex gap-2 items-center">
                <Badge>Express</Badge>
                <Badge>TypeScript</Badge>
              </div>
            </li>

            <li className="space-y-2 mb-4">
              <Typography variant="h4">Aplikacja mobilna</Typography>
              <div className="flex gap-2 items-center ml-4">
                <Typography className="font-semibold">Jakub</Typography>
                <Typography className="italic" variant="muted">
                  Kuba27x
                </Typography>
                <Button asChild className="p-0 text-muted-foreground ml-4" variant="link">
                  <a href="https://github.com/Kuba27x" rel="noreferrer" target="_blank">
                    <ExternalLink size="4" />
                    Zobacz profil GitHub
                  </a>
                </Button>
              </div>

              <div className="flex gap-2 items-center ml-4">
                <Typography className="font-semibold">Ewelina</Typography>
                <Typography className="italic" variant="muted">
                  evee03
                </Typography>
                <Button asChild className="p-0 text-muted-foreground ml-4" variant="link">
                  <a href="https://github.com/evee03" rel="noreferrer" target="_blank">
                    <ExternalLink size="4" />
                    Zobacz profil GitHub
                  </a>
                </Button>
              </div>

              <div className="ml-4 flex gap-2 items-center">
                <Badge>Kotlin</Badge>
                <Badge>Jetpack Compose </Badge>
              </div>
            </li>
          </ul>
        </div>
      </DialogContent>
    </Dialog>
  );
}

interface FooterDialogHeaderProps {
  title: string;
  description?: string;
  onClose: () => void;
}

function FooterDialogHeader({ title, description, onClose }: FooterDialogHeaderProps) {
  return (
    <DialogHeader className="grid grid-cols-[1fr_auto_1fr] items-center gap-4 p-4">
      <div className="col-start-2">
        <DialogTitle className="truncate">{title}</DialogTitle>
        <DialogDescription className="sr-only">{description}</DialogDescription>
      </div>

      <Button
        className="justify-self-end col-start-3 row-start-1"
        onClick={onClose}
        size="icon"
        type="button"
        variant="outline"
      >
        <X />
      </Button>
    </DialogHeader>
  );
}

interface FooterMediaLinkGroupProps {
  title: string;
}

function FooterMediaLinkGroup({ title, children }: PropsWithChildren<FooterMediaLinkGroupProps>) {
  return (
    <div>
      <Typography>{title}</Typography>
      <ul className="list-decimal ml-8 text-sm">{children}</ul>
    </div>
  );
}

function FooterMediaLink({ to }: { to: string }) {
  return (
    <li>
      <a className="text-primary hover:underline" href={to} rel="noreferrer" target="_blank">
        {to}
      </a>
    </li>
  );
}

export default Footer;
