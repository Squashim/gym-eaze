import ReusablePagination from '@/components/ui/reusable-pagination';
import ReportList from './report-list';
import useReportQuery from '@/hooks/filters-pagination/use-report-query';
import useGetReports from '@/hooks/query/reports/use-get-reports';
import { useMemo, useRef } from 'react';

function ReportsContent() {
  const { params, setPage, setFilters, setPageSize, clearFilters } = useReportQuery();
  const { data: reports, isLoading, isError } = useGetReports(params);
  const scrollRef = useRef<HTMLDivElement>(null);

  const totalPages = useMemo(() => {
    return reports ? Math.ceil(reports.total / reports.pageSize) : 0;
  }, [reports]);

  return (
    <div className="grid gap-6" ref={scrollRef}>
      {/* filtry */}
      <ReportList isError={isError} isLoading={isLoading} reports={reports?.items} />

      <ReusablePagination
        currentPage={params.page}
        onPageChange={setPage}
        onPageSizeChange={setPageSize}
        pageSize={params.pageSize}
        scrollToRef={scrollRef}
        totalPages={totalPages}
      />
    </div>
  );
}

export default ReportsContent;
