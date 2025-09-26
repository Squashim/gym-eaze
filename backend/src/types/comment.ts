import { AllParentTypes } from '../constants/commentType';

export interface CreateCommentBody {
  content: string;
  parentType: AllParentTypes;
  parentId: string;
}
