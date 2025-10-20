import ReusablePagination from '@/components/ui/reusable-pagination';
import ReportList from './report-list';
import useReportQuery from '@/hooks/filters-pagination/use-report-query';
import useGetReports from '@/hooks/query/reports/use-get-reports';
import { useMemo, useRef } from 'react';
import ReportFilters from './report-filters';
import Typography from '@/components/ui/typography';
import ScrollToTopButton from '@/components/ui/scroll-to-top-button';

function ReportsContent() {
  const { params, setPage, setFilters, setPageSize, clearFilters } = useReportQuery();
  const { data: reports, isLoading, isError } = useGetReports(params);
  const scrollRef = useRef<HTMLDivElement>(null);

  const totalPages = useMemo(() => {
    return reports ? Math.ceil(reports.total / reports.pageSize) : 0;
  }, [reports]);

  return (
    <div className="grid gap-4" ref={scrollRef}>
      <ReportFilters clearFilters={clearFilters} filters={params.filters} setFilters={setFilters} />
      <Typography className="ml-1" variant="h4">
        {isLoading ? 'Ładowanie zgłoszeń...' : `Zgłoszenia (${reports ? reports.items.length : 0})`}
      </Typography>
      <ReportList isError={isError} isLoading={isLoading} reports={reports?.items} />
      <ReusablePagination
        currentPage={params.page}
        onPageChange={setPage}
        onPageSizeChange={setPageSize}
        pageSize={params.pageSize}
        scrollToRef={scrollRef}
        totalPages={totalPages}
      />
      <ScrollToTopButton />
    </div>
  );
}

export default ReportsContent;
