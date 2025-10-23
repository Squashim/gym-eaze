import { getById } from '@/api/post';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useQuery } from '@tanstack/react-query';

function useGetPost(id: string, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: QUERY_KEYS.POST(id),
    queryFn: () => getById(id),
    retry: false,
    refetchOnWindowFocus: false,
    ...options,
  });
}

export default useGetPost;
