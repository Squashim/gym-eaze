import { getAll } from '@/api/report';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import type { PaginatedResponse, ReportQueryParams } from '@/types/filters';
import type { GetReportsApiResponse } from '@/types/report';
import { useQuery } from '@tanstack/react-query';

function useGetReports(params: ReportQueryParams) {
  return useQuery<PaginatedResponse<GetReportsApiResponse>>({
    queryKey: QUERY_KEYS.REPORTS(params),
    queryFn: () => getAll(params),
    retry: false,
  });
}

export default useGetReports;
