export const ItemType = {
  Post: 'Post',
  Comment: 'Komentarz',
  Exercise: 'Ćwiczenie',
  Training: 'Trening',
} as const;

export type ItemType = (typeof ItemType)[keyof typeof ItemType];
