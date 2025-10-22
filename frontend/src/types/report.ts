import type { ItemType } from '@/constants/enums/item-type';
import type { ReportStatus, ReportType } from '@/constants/enums/report-type';
import type { Author } from './user';

export type CreateReportPayload = {
  reportedItemId: string;
  reportedItemType: ItemType;
  reportType: ReportType;
  reason?: string;
};

export type UpdateReportStatusPayload = {
  itemId: string;
  itemType: ItemType;
  status: ReportStatus;
};

export type GetReportsApiResponse = {
  reportedItemId: string;
  reportedItemType: ItemType;
  count: number;
  status: ReportStatus;
  lastReportedAt: string;
  reportedItem: {
    _id: string;
    author: Author & { isBlocked?: boolean };
    content?: string;
    name?: string;
    parentType?: ItemType;
    parentId?: string;
    createdAt: string;
  };
  reviewedAt?: string;
};

export type GetReportApiResponse = {
  _id: string;
  reporterId: Author;
  reportType: ReportType;
  reason?: string;
  createdAt: string;
};
