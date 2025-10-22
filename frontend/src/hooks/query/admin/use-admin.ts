import { ItemType } from '@/constants/enums/item-type';
import { useMutation } from '@tanstack/react-query';
import { blockUser, deleteContent } from '@/api/admin';

export function useAdmin() {
  const deleteContentMutation = useMutation({
    mutationFn: ({ itemId, itemType }: { itemId: string; itemType: ItemType }) =>
      deleteContent(itemId, itemType),
  });

  const blockUserMutation = useMutation({
    mutationFn: (userId: string) => blockUser(userId),
  });

  return {
    deleteContent: deleteContentMutation,
    blockUser: blockUserMutation,
  };
}
