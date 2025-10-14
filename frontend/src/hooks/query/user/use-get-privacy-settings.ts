import { getPrivacySettings } from '@/api/user';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetPrivacySettings(options?: { enabled?: boolean }) {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_PRIVACY_SETTINGS,
    queryFn: getPrivacySettings,
    retry: false,
    enabled: !!user && (options?.enabled ?? true),
  });
}

export default useGetPrivacySettings;
