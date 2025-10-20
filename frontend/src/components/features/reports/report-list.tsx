import type { GetReportsApiResponse } from '@/types/report';
import ReportGroup from './report-group';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Info } from 'lucide-react';

interface ReportListProps {
  isLoading: boolean;
  isError: boolean;
  reports?: GetReportsApiResponse[];
}

function ReportList({ isLoading, isError, reports }: ReportListProps) {
  if (isLoading) {
    return Array.from({ length: 5 }).map((_, index) => (
      <Skeleton className="h-[280px]" key={index} />
    ));
  }

  if (isError || !reports) {
    return (
      <Alert>
        <Info />
        <AlertTitle>Wystąpił błąd podczas ładowania zgłoszeń.</AlertTitle>
        <AlertDescription>Spróbuj odświeżyć stronę lub wróć później.</AlertDescription>
      </Alert>
    );
  }

  if (reports.length === 0) {
    return (
      <Alert>
        <Info />
        <AlertTitle>Brak zgłoszeń do wyświetlenia.</AlertTitle>
        <AlertDescription>Nie ma obecnie żadnych zgłoszeń.</AlertDescription>
      </Alert>
    );
  }

  return reports.map((report) => <ReportGroup key={report.reportedItemId} report={report} />);
}

export default ReportList;
