import { CommentParentType } from '../constants/commentType';
import { ReportType } from '../constants/reportType';
import { StatusReport } from '../constants/statuses';
import { PageSize } from './trainingPlan';

export interface CreateReportPayload {
  reportedItemId: string;
  reportedItemType: CommentParentType;
  reportType: ReportType;
  reason?: string;
}

export type ReportFilters = {
  reportedItemType?: CommentParentType;
  status?: StatusReport;
};

export type ReportQuery = {
  filters?: ReportFilters;
  page: number;
  pageSize: PageSize;
};
