import type { PageSize, PaginatedQueryParams } from '@/types/filters';
import { useCallback, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';

function usePaginationQuery() {
  const [searchParams, setSearchParams] = useSearchParams();

  const page = Number(searchParams.get('page') ?? 1);
  const pageSize = (Number(searchParams.get('pageSize')) as PageSize) || 9;

  const params: PaginatedQueryParams = useMemo(() => {
    return {
      page,
      pageSize,
    };
  }, [page, pageSize]);

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
    setPage: (newPage: number) => setUrlParams(newPage),
    setPageSize: (newPageSize: PageSize) => setUrlParams(1, newPageSize),
  };
}

export default usePaginationQuery;
