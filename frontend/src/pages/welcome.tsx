import PageLayout from '@/components/layout/page';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import Typography from '@/components/ui/typography';
import usePageTitle from '@/hooks/utils/use-page-title';
import image1 from '@/assets/images/welcome-page-1.webp';
import image2 from '@/assets/images/welcome-page-2.webp';
import image3 from '@/assets/images/welcome-page-3.webp';
import image4 from '@/assets/images/welcome-page-4.webp';
import { Button } from '@/components/ui/button';
import { Link } from 'react-router-dom';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import { BadgeCheck, BicepsFlexed, CalendarDays, Check, Smartphone, Users, X } from 'lucide-react';

const Welcome = () => {
  usePageTitle('Witamy w GymEaze');
  return (
    <PageLayout>
      <CtaSection />
      <FunctionalitiesSection />
      <FaqSection />
      <ScrollToTopButton />
    </PageLayout>
  );
};

function CtaSection() {
  return (
    <section className="grid grid-cols-1 md:grid-cols-[1fr_0.6fr] gap-8">
      <div className="flex flex-col gap-2 justify-center">
        <Typography className="text-left md:text-5xl" variant="h1">
          Trenuj, twórz i buduj społeczność
        </Typography>
        <Typography variant="lead">Społecznościowy system wspomagający treningi</Typography>
        <Typography className="text-balance mt-2">
          GymEaze to serwis pozwalający na tworzenie, przeglądanie planów treningowych i ćwiczeń.
          Dodatkowo wyposażony jest w aspekt społecznościowy, poprzez system postów, obserwacji,
          komentarzy i zgłaszania. W skład serwisu wchodzi również aplikacja mobilna, dostępna na
          telefonach z systemem Android, która ułatwia śledzenie i wykonywanie planów treningowych.
          Dołącz do naszej społeczności już dziś!
        </Typography>

        <div className="flex flex-col sm:flex-row gap-4 mt-4">
          <Button asChild className="text-lg py-6" size="lg">
            <Link to="/signup">Rozpocznij za darmo</Link>
          </Button>
          <Button asChild className="text-lg py-6" size="lg" variant="outline">
            <Link to="/login">Mam już konto</Link>
          </Button>
        </div>

        <Separator className="my-8" />

        <div className="flex items-center justify-evenly gap-4 flex-wrap">
          <Badge
            className="bg-accent/30 gap-3 px-4 py-3 hover:bg-gradient-to-br hover:from-accent/40 hover:to-secondary/10 transition-colors border border-primary/10 ease-in-out flex-1 w-full min-w-[200px]"
            variant="accent"
          >
            <div>
              <BicepsFlexed className="size-8 text-secondary" />
            </div>
            <div className="flex flex-col">
              <Typography variant="h4">50+</Typography>
              <Typography variant="muted">dostępnych ćwiczeń</Typography>
            </div>
          </Badge>

          <Badge
            className="bg-accent/30 gap-3 px-4 py-3 hover:bg-gradient-to-br hover:from-accent/40 hover:to-secondary/10 transition-colors border border-primary/10 ease-in-out flex-1 w-full min-w-[200px]"
            variant="accent"
          >
            <div>
              <CalendarDays className="size-8 text-secondary" />
            </div>
            <div className="flex flex-col">
              <Typography variant="h4">5+</Typography>
              <Typography variant="muted">planów treningowych</Typography>
            </div>
          </Badge>

          <Badge
            className="bg-accent/30 gap-3 px-4 py-3 hover:bg-gradient-to-br hover:from-accent/40 hover:to-secondary/10 transition-colors border border-primary/10 ease-in-out flex-1 w-full min-w-[200px]"
            variant="accent"
          >
            <div>
              <BadgeCheck className="size-8 text-secondary" />
            </div>
            <div className="flex flex-col">
              <Typography variant="h4">20+</Typography>
              <Typography variant="muted">odznak do zdobycia</Typography>
            </div>
          </Badge>
        </div>
      </div>

      <div className="w-full justify-center items-center relative max-w-[400px] max-h-[600px] overflow-hidden hidden md:flex mx-auto">
        <img
          alt="Aplikacja GymEaze na telefonie pokazująca intuicyjny interfejs do planowania treningów"
          className="w-full h-auto aspect-auto object-cover pt-24 scale-110"
          loading="lazy"
          src={image1}
        />
      </div>
    </section>
  );
}

function FunctionalitiesSection() {
  return (
    <section className="space-y-4 my-4">
      <Typography className="text-center border-0" variant="h2">
        Odkryj najważniejsze funkcje
      </Typography>

      <div className="space-y-16 my-16">
        <div className="grid grid-cols-1 lg:grid-cols-[0.8fr_1fr] gap-8 items-center">
          <div className="flex flex-col gap-3 ">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <CalendarDays className="size-6 text-primary" />
              </div>
              <Typography variant="h3">Twórz własne plany i ćwiczenia</Typography>
            </div>
            <Typography className="text-base" variant="muted">
              Buduj swoje plany treningowe wybierając z biblioteki publicznych ćwiczeń lub dodając
              własne ćwiczenia. Określaj dni treningowe i wolne, ustalaj liczbę serii i powtórzeń.
              Wszystko to w intuicyjnym kreatorze.
            </Typography>
            <div className="flex flex-wrap gap-2">
              <Badge>Rozbudowane kreatory</Badge>
              <Badge>Biblioteka publicznych ćwiczeń</Badge>
              <Badge>Całkowita swoboda tworzenia</Badge>
            </div>
          </div>
          <img
            alt="Ekran przedstawiający widok kreatora planów wraz z włączoną biblioteką ćwiczeń"
            className="aspect-auto rounded-xl border border-border shadow-xl hover:scale-105 transition-transform duration-300"
            src={image2}
          />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-[0.8fr_1fr] gap-8 items-center">
          <img
            alt="Ekran przedstawiający widok wykonywanego ćwiczenia podczas treningu w aplikacji mobilnej"
            className="aspect-auto rounded-xl border border-border shadow-xl hover:scale-105 transition-transform duration-300 max-w-[300px] mx-auto"
            src={image3}
          />

          <div className="flex flex-col gap-3 row-start-1 lg:col-start-2">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <Smartphone className="size-6 text-primary" />
              </div>
              <Typography variant="h3">Trenuj z aplikacją mobilną</Typography>
            </div>
            <Typography className="text-base" variant="muted">
              Wybieraj spośród gotowych planów lub stwórz własny, a następnie wykonuj treningi krok
              po kroku. Każda sesja treningowa zawiera wybrane przez Ciebie ćwiczenia z liczbą serii
              i powtórzeń, a aplikacja pomaga Ci śledzić postępy i odliczać czas.
            </Typography>
            <div className="flex flex-wrap gap-2">
              <Badge>Podgląd wideo aktualnego ćwiczenia</Badge>
              <Badge>Nawigacja pomiędzy seriami</Badge>
              <Badge>Serie powtórzeniowe i czasowe</Badge>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-[0.8fr_1fr] gap-8 items-center">
          <div className="flex flex-col gap-3 ">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <Users className="size-6 text-primary" />
              </div>
              <Typography variant="h3">Buduj społeczność</Typography>
            </div>
            <Typography className="text-base" variant="muted">
              Udostępniaj swoje plany treningowe i ćwiczenia innym użytkownikom, komentuj i dodawaj
              polubienia do treści innych. Obserwuj innych użytkowników, aby być na bieżąco z ich
              nowymi planami i postępami. Zdobywaj odznaki za aktywność i osiągnięcia w serwisie.
            </Typography>
            <div className="flex flex-wrap gap-2">
              <Badge>System obserwacji i zgłaszania</Badge>
              <Badge>Odznaki</Badge>
              <Badge>Udostępnianie ćwiczeń i planów</Badge>
              <Badge>Komentarze i polubienia</Badge>
            </div>
          </div>

          <img
            alt="Ekran przedstawiający widok własnego profilu wraz z najnowszymi postami"
            className="aspect-auto rounded-xl border border-border shadow-xl hover:scale-105 transition-transform duration-300 mx-auto"
            src={image4}
          />
        </div>
      </div>
    </section>
  );
}

function FaqSection() {
  return (
    <section className="space-y-4 my-4">
      <Typography className="text-center border-0" variant="h2">
        Często zadawane pytania
      </Typography>
      <div className="flex flex-col gap-4 px-4 border border-border rounded-md bg-background/20 backdrop-blur-[4px]">
        <Accordion className="w-full" collapsible defaultValue="account" type="single">
          <AccordionItem value="account">
            <AccordionTrigger className="text-lg cursor-pointer">
              Czy mogę korzystać z serwisu bez zakładania konta?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography className="leading-normal">
                Tak, jednakże użytkownik bez konta ma ograniczony dostęp do funkcji. Poniżej
                znajduje się tabela przedstawiająca różnice między użytkownikiem zalogowanym i
                niezalogowanym:
              </Typography>
              <div className="mt-4 overflow-x-auto scrollbar">
                <table className="w-full border-collapse border border-border rounded-lg overflow-hidden">
                  <thead>
                    <tr className="bg-muted/50">
                      <th className="border border-border px-4 py-3 text-left font-medium">
                        Funkcja
                      </th>
                      <th className="border border-border px-4 py-3 text-center font-medium">
                        Niezalogowany
                      </th>
                      <th className="border border-border px-4 py-3 text-center font-medium">
                        Zalogowany
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <TableRow title="Przeglądanie publicznych planów, ćwiczeń i postów" />
                    <TableRow title="Przeglądanie szczegółów planów treningowych i ćwiczeń" />
                    <TableRow
                      noAccount={false}
                      title="Tworzenie własnych planów treningowych, ćwiczeń i postów"
                    />
                    <TableRow
                      noAccount={false}
                      title="Zapisywanie planów treningowych i ćwiczeń do ulubionych"
                    />
                    <TableRow
                      noAccount={false}
                      title="Rozpoczynanie i śledzenie postępów w planach treningowych"
                    />
                    <TableRow
                      noAccount={false}
                      title="Pobranie plików PDF z planami treningowymi"
                    />
                    <TableRow noAccount={false} title="Pisanie komentarzy i dodawanie polubień" />
                    <TableRow noAccount={false} title="Przeglądanie profili innych użytkowników" />
                    <TableRow noAccount={false} title="Zdobywanie odznak" />
                  </tbody>
                </table>
              </div>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="mobile-vs-desktop">
            <AccordionTrigger className="text-lg cursor-pointer">
              Czy muszę korzystać z aplikacji mobilnej czy mogę używać tylko wersji przeglądarkowej?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography className="leading-normal">
                Serwis GymEaze został zaprojektowany w taki sposób, aby większość funkcji była
                dostępna zarówno w aplikacji mobilnej, jak i w wersji przeglądarkowej. Jednakże
                pojawiają się pewne różnice, takie jak:
              </Typography>
              <ul className="list-disc ml-4 mt-2 space-y-1">
                <li>
                  Aplikacja mobilna oferuje funkcje związane z wykonywaniem i śledzeniem aktywnych
                  treningów.
                </li>
                <li>
                  Wersja przeglądarkowa może oferować bardziej rozbudowane narzędzia do tworzenia
                  planów treningowych i ćwiczeń ze względu na większą funkcjonalność i wygodę
                  tworzenia.
                </li>
              </ul>
            </AccordionContent>
          </AccordionItem>
          <AccordionItem value="mobile-desktop-sync">
            <AccordionTrigger className="text-lg cursor-pointer">
              Czy aplikacja mobilna i wersja przeglądarkowa są ze sobą zsynchronizowane?
            </AccordionTrigger>
            <AccordionContent className="text-balance">
              <Typography>
                Wszystkie dane są automatycznie synchronizowane między aplikacją mobilną a
                przeglądarkową. Oznacza to, że niezależnie od tego, czy korzystasz z aplikacji na
                telefonie, czy z serwisu przeglądarkowego, Twoje postępy, plany treningowe i inne
                dane będą zawsze aktualne i dostępne.
              </Typography>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
    </section>
  );
}

interface TableRowProps {
  title: string;
  hasAccount?: boolean;
  noAccount?: boolean;
}

function TableRow({ title, hasAccount = true, noAccount = true }: TableRowProps) {
  return (
    <tr>
      <td className="border border-border px-4 py-2">{title}</td>
      <td className="border border-border px-4 py-2 text-center">
        {noAccount ? (
          <Check className="text-secondary mx-auto" />
        ) : (
          <X className="text-destructive mx-auto" />
        )}
      </td>
      <td className="border border-border px-4 py-2 text-center">
        {hasAccount ? (
          <Check className="text-secondary mx-auto" />
        ) : (
          <X className="text-destructive mx-auto" />
        )}
      </td>
    </tr>
  );
}

export default Welcome;
