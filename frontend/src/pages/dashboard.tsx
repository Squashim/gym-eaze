import ReportsContent from '@/components/features/reports/reports-content';
import PageLayout from '@/components/layout/page';
import usePageTitle from '@/hooks/utils/use-page-title';

function Dashboard() {
  usePageTitle('Panel administracyjny');

  return (
    <PageLayout title="Panel zarządzania">
      <ReportsContent />
    </PageLayout>
  );
}

export default Dashboard;
