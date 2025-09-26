import type { AdvancedLevel } from '@/constants/advanced-level';

export type SortBy = 'popular' | 'top-rated' | 'latest';

export type PageSize = 9 | 18 | 36;

export type PageType = 'browser' | 'user' | 'favorite';

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

export type ExerciseFilters = {
  search?: string;
  muscleGroups?: string[];
  equipment?: string[];
  hashtags?: string[];
  difficulty?: AdvancedLevel;
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
