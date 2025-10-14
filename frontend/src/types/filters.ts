import type { AdvancedLevel } from '@/constants/enums/advanced-level';
import type { Goal } from '@/constants/enums/goals';
import type { ItemType } from '@/constants/enums/item-type';
import type { StatusReport } from '@/constants/enums/report-type';

export type SortBy = 'popular' | 'top-rated' | 'latest';

export type PageSize = 9 | 18 | 36;

export type PageType = 'browser' | 'user' | 'favorite' | 'profile';

export interface FilteredQueryParams<TFilters> {
  sortBy: SortBy;
  filters?: TFilters;
  page: number;
  pageSize: PageSize;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: PageSize;
}

export type PaginatedQueryParams = {
  page: number;
  pageSize: PageSize;
};

export type ExerciseQueryParams = FilteredQueryParams<ExerciseFilters>;
export type WorkoutQueryParams = FilteredQueryParams<WorkoutFilters>;

export type ExerciseFilters = {
  search?: string;
  muscleGroups?: string[];
  equipment?: string[];
  hashtags?: string[];
  difficulty?: AdvancedLevel;
  followers?: boolean;
};

export type WorkoutFilters = {
  search?: string;
  goal?: Goal;
  difficulty?: AdvancedLevel;
  minDuration?: number;
  maxDuration?: number;
  hashtags?: string[];
  followers?: boolean;
};

export interface CursorPaginatedResponse<T> {
  items: T[];
  hasMore: boolean;
  nextCursor: string | null;
}

export type PostQueryParams = {
  hashtags?: string[];
  followedOnly?: boolean;
  cursor?: string | null;
};

export type ReportFilters = {
  reportedItemType?: ItemType;
  status?: StatusReport;
};

export type ReportQueryParams = {
  filters: ReportFilters;
  page: number;
  pageSize: PageSize;
};
