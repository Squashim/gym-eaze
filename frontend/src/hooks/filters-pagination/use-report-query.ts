import { defaultReportFilters } from '@/constants/defaults/filters';
import type { PageSize, ReportFilters, ReportQueryParams } from '@/types/filters';
import { useCallback, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

function useReportQuery() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useState<ReportFilters>(defaultReportFilters);

  const page = Number(searchParams.get('page') ?? 1);
  const pageSize = (Number(searchParams.get('pageSize')) as PageSize) || 9;

  const params: ReportQueryParams = useMemo(() => {
    return {
      filters,
      page,
      pageSize,
    };
  }, [filters, page, pageSize]);

  const setFiltersParams = useCallback(
    (newFilters: Partial<ReportFilters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));

      const urlParams = new URLSearchParams(searchParams);
      urlParams.set('page', '1');
      urlParams.set('pageSize', pageSize.toString());

      setSearchParams(urlParams);
    },
    [pageSize, setSearchParams, searchParams],
  );

  const clearFilters = useCallback(() => {
    setFilters(defaultReportFilters);

    const urlParams = new URLSearchParams();
    urlParams.set('page', '1');
    urlParams.set('pageSize', pageSize.toString());
    setSearchParams(urlParams);
  }, [pageSize, setSearchParams]);

  const setUrlParams = useCallback(
    (newPage?: number, newPageSize?: number) => {
      const urlParams = new URLSearchParams(searchParams);

      if (newPage !== undefined) {
        urlParams.set('page', newPage.toString());
      } else {
        urlParams.set('page', page.toString());
      }

      urlParams.set('pageSize', (newPageSize ?? pageSize).toString());

      setSearchParams(urlParams);
    },
    [page, pageSize, searchParams, setSearchParams],
  );

  return {
    params,
    filters,
    setFilters: setFiltersParams,
    clearFilters,
    setPage: (newPage: number) => setUrlParams(newPage),
    setPageSize: (newPageSize: PageSize) => setUrlParams(1, newPageSize),
  };
}

export default useReportQuery;
