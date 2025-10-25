import PageLayout from '@/components/layout/page';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';
import usePageTitle from '@/hooks/utils/use-page-title';

const Welcome = () => {
  usePageTitle('Witamy w GymEaze');
  return (
    <PageLayout>
      <ScrollToTopButton />
    </PageLayout>
  );
};

export default Welcome;
