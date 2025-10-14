import type { ItemType } from '@/constants/enums/item-type';
import type { ReportType, StatusReport } from '@/constants/enums/report-type';
import type { Author } from './user';

export type CreateReportPayload = {
  reportedItemId: string;
  reportedItemType: ItemType;
  reportType: ReportType;
  reason?: string;
};

export type GetReportsApiResponse = {
  reportedItemId: string;
  reportedItemType: ItemType;
  count: number;
  status: StatusReport;
  lastReportedAt: string;
  reportedItem: {
    _id: string;
    author: Author;
    content?: string;
    name?: string;
    createdAt: string;
  };
  reviewedAt?: string;
};
