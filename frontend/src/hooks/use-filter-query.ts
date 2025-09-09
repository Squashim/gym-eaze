import type { ExerciseFilters, FilteredQueryParams, PageSize, SortBy } from '@/types/filters';
import { useCallback, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

function useFilterQuery<TFilters>(initialFilters: TFilters) {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useState<TFilters>(initialFilters);

  const page = Number(searchParams.get('page') ?? 1);
  const pageSize = (Number(searchParams.get('pageSize')) as PageSize) || 9;
  const sortBy = (searchParams.get('sortBy') as SortBy) ?? 'latest';

  const params: FilteredQueryParams<TFilters> = useMemo(() => {
    return {
      filters,
      page,
      pageSize,
      sortBy,
    };
  }, [filters, page, pageSize, sortBy]);

  const setFiltersParams = useCallback(
    (newFilters: Partial<ExerciseFilters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));

      const isSearchOnly = Object.keys(newFilters).length === 1 && 'search' in newFilters;
      const urlParams = new URLSearchParams(searchParams);
      if (!isSearchOnly) {
        urlParams.set('page', '1');
      }
      urlParams.set('pageSize', pageSize.toString());
      urlParams.set('sortBy', sortBy);

      setSearchParams(urlParams);
    },
    [pageSize, sortBy, setSearchParams, searchParams],
  );

  const clearFilters = useCallback(() => {
    setFilters(initialFilters);

    const urlParams = new URLSearchParams();
    urlParams.set('page', '1');
    urlParams.set('pageSize', pageSize.toString());
    urlParams.set('sortBy', sortBy);
    setSearchParams(urlParams);
  }, [initialFilters, pageSize, sortBy, setSearchParams]);

  const setUrlParams = useCallback(
    (newPage?: number, newSortBy?: SortBy, newPageSize?: number) => {
      const urlParams = new URLSearchParams(searchParams);

      if (newPage !== undefined) {
        urlParams.set('page', newPage.toString());
      } else {
        urlParams.set('page', page.toString());
      }

      urlParams.set('pageSize', (newPageSize ?? pageSize).toString());
      urlParams.set('sortBy', newSortBy ?? sortBy);

      setSearchParams(urlParams);
    },
    [page, pageSize, sortBy, searchParams, setSearchParams],
  );

  return {
    params,
    filters,
    setFilters: setFiltersParams,
    clearFilters,
    setPage: (newPage: number) => setUrlParams(newPage),
    setSortBy: (newSortBy: SortBy) => setUrlParams(1, newSortBy),
    setPageSize: (newPageSize: PageSize) => setUrlParams(1, undefined, newPageSize),
  };
}

export default useFilterQuery;
