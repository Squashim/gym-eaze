import type { ItemType } from '@/constants/enums/item-type';
import type { ReportType } from '@/constants/enums/report-type';

export type CreateReportPayload = {
  reportedItemId: string;
  reportedItemType: ItemType;
  reportType: ReportType;
  reason?: string;
};
