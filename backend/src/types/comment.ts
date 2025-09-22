export interface CreateCommentBody {
  content: string;
  parentType: 'Post' | 'Ćwiczenie' | 'Trening';
  parentId: string;
}
