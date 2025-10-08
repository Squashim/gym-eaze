import { create, remove, toggleLike, update } from '@/api/comment';
import queryClient from '@/config/query-client';
import type { ItemType } from '@/constants/item-type';
import { QUERY_KEYS } from '@/constants/query-keys';
import { useUser } from '@/context/user';
import usePostParamsQuery from '@/hooks/use-post-params-query';
import { getApiErrorMsg } from '@/lib/utils';
import type { UpdateCommentPayload } from '@/types/comment';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

interface useCommentProps {
  parentType: ItemType;
  parentId: string;
}

function isItemReplyCommentsQuery(queryKey: unknown): boolean {
  return (
    Array.isArray(queryKey) &&
    queryKey.length === 3 &&
    queryKey[2] === 'comments' &&
    (queryKey[0] === 'posts' || queryKey[0] === 'exercises' || queryKey[0] === 'workouts')
  );
}

export function useComment({ parentType, parentId }: useCommentProps) {
  const { user } = useUser();
  const { params } = usePostParamsQuery();

  const createMutation = useMutation({
    mutationFn: create,
    onSuccess: async (response) => {
      if (parentType === 'Post') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POST_COMMENTS(parentId) });
      } else if (parentType === 'Komentarz') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.COMMENT_REPLIES(parentId) });
        await queryClient.invalidateQueries({
          predicate: (query) => isItemReplyCommentsQuery(query.queryKey),
        });
      } else if (parentType === 'Ćwiczenie') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE(parentId) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE_COMMENTS(parentId) });
      } else if (parentType === 'Trening') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT(parentId) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT_COMMENTS(parentId) });
      }
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania komentarza/odpowiedzi. Spróbuj ponownie później.',
        );
      }
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: UpdateCommentPayload }) =>
      update(id, payload),
    onSuccess: async (response) => {
      if (parentType === 'Post') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POST_COMMENTS(parentId) });
      } else if (parentType === 'Komentarz') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.COMMENT_REPLIES(parentId) });
      } else if (parentType === 'Ćwiczenie') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE_COMMENTS(parentId) });
      } else if (parentType === 'Trening') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT_COMMENTS(parentId) });
      }
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas edycji komentarza/odpowiedzi. Spróbuj ponownie później.',
        );
      }
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => remove(id),
    onSuccess: async (response) => {
      if (parentType === 'Post') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POSTS(params) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POST_COMMENTS(parentId) });
      } else if (parentType === 'Komentarz') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.COMMENT_REPLIES(parentId) });
        await queryClient.invalidateQueries({
          predicate: (query) => isItemReplyCommentsQuery(query.queryKey),
        });
      } else if (parentType === 'Ćwiczenie') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE(parentId) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE_COMMENTS(parentId) });
      } else if (parentType === 'Trening') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT(parentId) });
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT_COMMENTS(parentId) });
      }

      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania komentarza/odpowiedzi. Spróbuj ponownie później.',
        );
      }
    },
  });

  const toggleLikeMutation = useMutation({
    mutationFn: (id: string) => toggleLike(id),
    onSuccess: async (response) => {
      if (parentType === 'Post') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.POST_COMMENTS(parentId) });
      } else if (parentType === 'Komentarz') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.COMMENT_REPLIES(parentId) });
      } else if (parentType === 'Ćwiczenie') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.EXERCISE_COMMENTS(parentId) });
      } else if (parentType === 'Trening') {
        await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.WORKOUT_COMMENTS(parentId) });
      }
      await queryClient.invalidateQueries({ queryKey: QUERY_KEYS.USER_LIKED_ITEMS(user!._id) });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas dodawania/usuwania polubienia. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createComment: createMutation,
    updateComment: updateMutation,
    toggleLikeComment: toggleLikeMutation,
    deleteComment: deleteMutation,
  };
}
