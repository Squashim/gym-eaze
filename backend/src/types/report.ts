import { CommentParentType } from '../constants/commentType';
import { ReportType } from '../constants/reportType';

export interface CreateReportPayload {
  reportedItemId: string;
  reportedItemType: CommentParentType;
  reportType: ReportType;
  reason?: string;
}
