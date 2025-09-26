export enum CommentParentType {
  exercise = 'Ćwiczenie',
  training = 'Trening',
  post = 'Post',
  comment = 'Komentarz',
}

export enum MainCommentParentType {
  exercise = 'Ćwiczenie',
  training = 'Trening',
  post = 'Post',
}

export type AllParentTypes = `${CommentParentType}`;
export type MainCommentParentTypes = `${MainCommentParentType}`;

export const commentParentTypeValues = Object.values(CommentParentType);
export const mainCommentParentTypes = Object.values(MainCommentParentType);
