import { toggleFollow } from '@/api/user';
import queryClient from '@/config/query-client';
import { QUERY_KEYS } from '@/constants/api/query-keys';
import { getApiErrorMsg } from '@/lib/utils';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function useUserFollow(username: string) {
  const toggleFollowUserMutation = useMutation({
    mutationFn: (id: string) => toggleFollow(id),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_FOLLOWING });
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_PROFILE(username) });

      toast.success(`${response.message}: ${username}`);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania/usuwania obserwacji. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    toggleFollow: toggleFollowUserMutation,
  };
}
