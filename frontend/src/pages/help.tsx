import PageLayout from '@/components/layout/page';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import Typography from '@/components/ui/typography';
import usePageTitle from '@/hooks/utils/use-page-title';
import { Lightbulb, Mail } from 'lucide-react';

function HelpPage() {
  usePageTitle('Pomoc - GymEaze');

  return (
    <PageLayout
      description="Znajdziesz tu odpowiedzi na najczęstsze pytania oraz wskazówki dotyczące korzystania z platformy."
      title="Pomoc"
    >
      <div className="flex flex-col gap-4 px-4 pt-4 border border-border rounded-md bg-background/20 backdrop-blur-[4px]">
        <Typography variant="h3">Ogólne informacje</Typography>
        <Accordion className="w-full" collapsible type="single">
          <AccordionItem value="mobile-desktop">
            <AccordionTrigger className="text-lg cursor-pointer">
              Czy GymEaze jest dostępne na urządzeniach mobilnych?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Tak! GymEaze jest dostępne na urządzeniach z systemem Android. Możesz pobrać
                aplikację aby zawsze mieć dostęp do wykonywania swoich planów treningowych w
                dowolnym miejscu, a także aby korzystać z inteligentnego czujnika.
              </Typography>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="mobile-desktop-sync">
            <AccordionTrigger className="text-lg cursor-pointer">
              Jak wygląda synchronizacja między aplikacją mobilną a przeglądarkową?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Wszystkie dane są automatycznie synchronizowane między aplikacją mobilną a
                przeglądarkową. Oznacza to, że niezależnie od tego, czy korzystasz z aplikacji na
                telefonie, czy z platformy w przeglądarce, Twoje postępy, plany treningowe i inne
                dane będą zawsze aktualne i dostępne.
              </Typography>
              <Typography className="mt-2">
                Jedyną różnicą jest to, że aplikacja mobilna oferuje wykonywanie i podgląd aktywnych
                treningów.
              </Typography>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="deleted-modified-plan">
            <AccordionTrigger className="text-lg cursor-pointer">
              Co w przypadku gdy ktoś usunie/zmodyfikuje plan, który rozpocząłem/am?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Jeśli plan treningowy, który rozpocząłeś/aś zostanie usunięty przez jego autora, lub
                zmodyfikowany w jakikolwiek sposób, to nie wpłynie to na Twój aktywny trening.
                Będziesz mógł/mogła kontynuować i ukończyć rozpoczęty plan zgodnie z jego oryginalną
                wersją w momencie jego rozpoczęcia.
              </Typography>
              <Typography>
                Gdy autor planu dokona zmian, aby je zobaczyć, będziesz musiał/musiała rozpocząć
                nowy trening z zaktualizowaną wersją planu.
              </Typography>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="multiple-devices">
            <AccordionTrigger className="text-lg cursor-pointer">
              Na ilu urządzeniach maksymalnie mogę korzystać z GymEaze?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Jednocześnie możesz być zalogowany/a na maksymalnie 5 urządzeniach. Jeśli spróbujesz
                zalogować się na kolejnym urządzeniu, zostaniesz wylogowany/a z najstarszego
                aktywnego urządzenia.
              </Typography>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>

      <div className="flex flex-col gap-4 px-4 pt-4 border border-border rounded-md bg-background/20 backdrop-blur-[4px]">
        <Typography variant="h3">Odznaki</Typography>
        <Alert className="border-primary/70">
          <Lightbulb />
          <AlertTitle className="line-clamp-none">Wskazówka</AlertTitle>
          <AlertDescription>
            Możesz przypiąć na swoim profilu do 5 odznak. Pokaż innym z czego jesteś dumny!
          </AlertDescription>
        </Alert>

        <Accordion className="w-full" collapsible type="single">
          <AccordionItem value="how-to-earn-badges">
            <AccordionTrigger className="text-lg cursor-pointer">
              Jak mogę zdobyć odznaki?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                W naszej społeczności każda aktywność ma znaczenie. Twój progres, zaangażowanie i
                wsparcie dla innych są nagradzane odznakami, które podkreślają Twoje osiągniecia i
                motywują do dalszego rozwoju.
              </Typography>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="badges-types">
            <AccordionTrigger className="text-lg cursor-pointer">
              Jakie są rodzaje odznak?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Wyróżniamy kilka kategorii odznak, które odzwierciedlają różne aspekty Twojej
                aktywności:
              </Typography>
              <ul className="list-disc pl-6 text-muted-foreground">
                <li>
                  <strong>Okres członkowstwa</strong> Za każdy rok aktywnego uczestnictwa w
                  społeczności GymEaze.
                </li>
                <li>
                  <strong>Czujnik</strong> Związane z korzystaniem z naszego czujnika do
                  inteligentych treningów.
                </li>
                <li>
                  <strong>Plany treningowe</strong> Związane z publikowanie planów treningowych dla
                  społeczności.
                </li>
                <li>
                  <strong>Ćwiczenia</strong> Związane z publikowanie ćwiczeń dla społeczności.
                </li>
                <li>
                  <strong>Aktywność społecznościowa</strong> Związane z publikowanie postów i
                  udzielaniem się publicznie w komentarzach.
                </li>
                <li>
                  <strong>Popularność</strong> Związane z liczbą osób obserwujących Twój profil.
                </li>
              </ul>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="badges-visibility">
            <AccordionTrigger className="text-lg cursor-pointer">
              Kto może zobaczyć moje odznaki?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Twoje odznaki po ich zdobyciu są niewidoczne, dopóki nie przypniesz ich do swojego
                profilu w{' '}
                <a className="underline" href="/profile/edit" title="Przejdź do edycji profilu">
                  ustawieniach profilu
                </a>
                . Możesz zmienić widoczność swoich odznak w dowolnym momencie korzystając z{' '}
                <a
                  className="underline"
                  href="/profile/privacy"
                  title="Przejdź do ustawień prywatności"
                >
                  ustawień prywatności
                </a>
                .
              </Typography>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>

      <div className="flex flex-col gap-4 p-4 border border-border rounded-md bg-background/20 backdrop-blur-[4px]">
        <Typography variant="h3">Wsparcie i błędy</Typography>
        <Typography className="max-w-4xl">
          Jeśli masz pytania, potrzebujesz pomocy lub znalazłeś/aś błąd podczas korzystania z
          platformy to skontaktuj się z naszym zespołem wsparcia poprzez poniższy adres e-mail:
        </Typography>
        <div>
          <a className="inline-flex gap-2 hover:underline" href="#">
            <Mail />
            gymeaze.pomoc@gmail.com
          </a>
        </div>
      </div>
    </PageLayout>
  );
}

export default HelpPage;
