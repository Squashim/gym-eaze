import { ItemType } from '@/constants/item-type';
import z from 'zod/v3';

const MAX_CONTENT_LENGTH = 1000;

export const commentSchema = z.object({
  parentType: z.nativeEnum(ItemType),
  parentId: z.string(),
  content: z
    .string()
    .min(1, 'Treść komentarza jest wymagana.')
    .refine(
      (val) => {
        const normalizedLength = val.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= MAX_CONTENT_LENGTH;
      },
      {
        message: 'Treść nie może być dłuższa niż 1000 znaków.',
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
        return normalizedLength <= MAX_CONTENT_LENGTH;
      },
      {
        message: 'Treść nie może być dłuższa niż 1000 znaków.',
      },
    ),
});

export type CommentSchema = z.infer<typeof commentSchema>;
export type UpdateCommentSchema = z.infer<typeof updateCommentSchema>;
