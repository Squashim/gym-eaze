import type { PageSize } from '@/types/filters';
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from './pagination';
import type { MouseEvent } from 'react';
import { cn } from '@/lib/utils';
import { useMediaQuery } from '@/hooks/use-media-query';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './select';
import Typography from './typography';

type ReusablePaginationProps = {
  currentPage: number;
  pageSize: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: PageSize) => void;
};

const ReusablePagination = ({
  currentPage,
  totalPages,
  pageSize,
  onPageChange,
  onPageSizeChange,
}: ReusablePaginationProps) => {
  const isMobile = useMediaQuery('(max-width: 555px)');
  const isTablet = useMediaQuery('(max-width: 1024px)');
  const delta = isMobile ? 0 : isTablet ? 1 : 3;

  const renderVisiblePages = () => {
    const range = [];
    const rangeWithDots = [];

    for (
      let i = Math.max(2, currentPage - delta);
      i <= Math.min(totalPages - 1, currentPage + delta);
      i++
    ) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, '...');
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push('...', totalPages);
    } else if (totalPages > 1) {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  const handlePreviousPage = (e: MouseEvent) => {
    e.preventDefault();
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNextPage = (e: MouseEvent) => {
    e.preventDefault();
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
    }
  };

  const handlePageSelect = (e: MouseEvent, page: number) => {
    e.preventDefault();
    if (page !== currentPage) {
      onPageChange(page);
    }
  };

  const visiblePages = renderVisiblePages();

  return (
    <div className="flex flex-col gap-4 w-full">
      <div className="flex items-center gap-2 w-full justify-end">
        <Typography variant="muted">Pokaż na stronie</Typography>

        <Select
          onValueChange={(value) => onPageSizeChange(Number(value) as PageSize)}
          value={pageSize.toString()}
        >
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="9">9</SelectItem>
            <SelectItem value="18">18</SelectItem>
            <SelectItem value="36">36</SelectItem>
          </SelectContent>
        </Select>
      </div>
      {totalPages > 1 && (
        <div className="flex justify-center w-full">
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  aria-disabled={currentPage <= 1}
                  className={cn(
                    currentPage <= 1 && 'pointer-events-none opacity-70',
                    !isMobile && 'h-10',
                  )}
                  href="#"
                  onClick={handlePreviousPage}
                />
              </PaginationItem>
              {visiblePages.map((page, index) => (
                <PaginationItem key={index}>
                  {page === '...' ? (
                    <PaginationEllipsis className={cn(!isMobile && 'size-10')} />
                  ) : (
                    <PaginationLink
                      className={cn(!isMobile && 'size-10')}
                      href="#"
                      isActive={currentPage === page}
                      onClick={(e) => handlePageSelect(e, page as number)}
                    >
                      {page}
                    </PaginationLink>
                  )}
                </PaginationItem>
              ))}
              <PaginationItem>
                <PaginationNext
                  aria-disabled={currentPage >= totalPages}
                  className={cn(
                    currentPage >= totalPages && 'pointer-events-none opacity-70',
                    !isMobile && 'h-10',
                  )}
                  href="#"
                  onClick={handleNextPage}
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      )}
    </div>
  );
};

export default ReusablePagination;
