import { COMMENT } from '@/constants/comment';
import { ItemType } from '@/constants/enums/item-type';
import z from 'zod/v3';

export const commentSchema = z.object({
  parentType: z.nativeEnum(ItemType),
  parentId: z.string(),
  content: z
    .string()
    .min(1, 'Treść komentarza jest wymagana.')
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= COMMENT.MAX_CONTENT_LENGTH;
      },
      {
        message: `Treść nie może być dłuższa niż ${COMMENT.MAX_CONTENT_LENGTH} znaków.`,
      },
    ),
});

export const updateCommentSchema = z.object({
  content: z
    .string()
    .min(1, 'Treść komentarza jest wymagana.')
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= COMMENT.MAX_CONTENT_LENGTH;
      },
      {
        message: `Treść nie może być dłuższa niż ${COMMENT.MAX_CONTENT_LENGTH} znaków.`,
      },
    ),
});

export type CommentSchema = z.infer<typeof commentSchema>;
export type UpdateCommentSchema = z.infer<typeof updateCommentSchema>;
