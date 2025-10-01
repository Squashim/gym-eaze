import PageLayout from '@/components/layout/page';
import usePageTitle from '@/hooks/utils/use-page-title';

function WorkoutBrowser() {
  usePageTitle('Przeglądaj plany');

  return (
    <PageLayout
      description="Przeglądaj plany treningowe udostępniane przez innych użytkowników. Możesz użyć filtrów, aby znaleźć te, które najlepiej pasują do Twoich potrzeb."
      title="Przeglądaj plany treningowe"
    ></PageLayout>
  );
}

export default WorkoutBrowser;
