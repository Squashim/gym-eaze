import type { GetReportsApiResponse } from '@/types/report';
import ReportGroup from './report-group';

interface ReportListProps {
  isLoading: boolean;
  isError: boolean;
  reports?: GetReportsApiResponse[];
}

function ReportList({ isLoading, isError, reports }: ReportListProps) {
  if (isLoading) {
    return <p>Ładowanie...</p>;
  }

  if (isError || !reports) {
    return <p>błąd ładowania</p>;
  }

  if (reports.length === 0) {
    return <p>Brak zgłoszeń</p>;
  }

  return reports.map((report) => <ReportGroup key={report.reportedItemId} report={report} />);
}

export default ReportList;
