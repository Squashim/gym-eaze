import { getPrivacySettings } from '@/api/user';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import { useQuery } from '@tanstack/react-query';

function useGetPrivacySettings() {
  const { user } = useUser();

  return useQuery({
    queryKey: QUERY_KEYS.USER_PRIVACY_SETTINGS,
    queryFn: getPrivacySettings,
    retry: false,
    enabled: !!user,
  });
}

export default useGetPrivacySettings;
