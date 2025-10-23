import { AlertTitle, Alert, AlertDescription } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Dialog, DialogClose, DialogContent, DialogFooter } from '@/components/ui/dialog';
import { InfoField } from '@/components/ui/info-fields';
import { ItemTypeBadge, ReportStatusBadge } from '@/components/ui/item-info-badges';
import ReusableAlertDialog from '@/components/ui/reusable-alert-dialog';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';
import type { ItemType } from '@/constants/enums/item-type';
import { ReportStatus } from '@/constants/enums/report-type';
import useGetPost from '@/hooks/query/posts/use-get-post';
import useGetReportItems from '@/hooks/query/reports/use-get-report-items';
import { useReport } from '@/hooks/query/reports/use-report';
import useOpen from '@/hooks/utils/use-open';
import { parsePersonReportedLabel, parseTimeAgo } from '@/lib/parsers';
import type { GetReportsApiResponse } from '@/types/report';
import { Ban, ChevronsUpDown, Eraser, ExternalLink, Info, Loader2, Trash2 } from 'lucide-react';
import { useCallback, useState } from 'react';
import Report from './report-item';
import PostDetails from '../posts/post-details';
import { useNotification } from '@/hooks/query/notifications/use-notification';
import { cn, getApiErrorMsg, isReportsQuery } from '@/lib/utils';
import { toast } from 'sonner';
import queryClient from '@/config/query-client';
import { useAdmin } from '@/hooks/query/admin/use-admin';

interface ReportGroupProps {
  report: GetReportsApiResponse;
  isAuthor: boolean;
}

function ReportGroup({ report, isAuthor }: ReportGroupProps) {
  const postDialog = useOpen();
  const blockUserDialog = useOpen();
  const deleteContentDialog = useOpen();
  const { createNotification } = useNotification();
  const { updateReportStatus, deleteReport } = useReport();
  const { blockUser, deleteContent } = useAdmin();

  const [showReportsDetails, setShowReportsDetails] = useState(false);

  const [warnLoading, setWarnLoading] = useState(false);
  const [dismissLoading, setDismissLoading] = useState(false);
  const [blockLoading, setBlockLoading] = useState(false);
  const [contentDeleteLoading, setContentDeleteLoading] = useState(false);

  const {
    data: reportItems,
    isLoading: isReportsLoading,
    isError: isReportsError,
  } = useGetReportItems(report.reportedItemId, report.reportedItemType, {
    enabled: showReportsDetails,
  });

  const getPostId = useCallback(() => {
    if (report.reportedItemType === 'Post') {
      return report.reportedItemId;
    }

    if (report.reportedItemType === 'Komentarz' && report.reportedItem!.parentType === 'Post') {
      return report.reportedItem!.parentId;
    }

    return null;
  }, [report]);

  const {
    data: postData,
    isLoading: isPostLoading,
    isError: isPostError,
  } = useGetPost(getPostId() || '', {
    enabled: postDialog.isOpen && getPostId() !== null,
  });

  const handleToggleReportsDetails = useCallback(() => {
    setShowReportsDetails((prev) => !prev);
  }, []);

  const handleShowPost = useCallback(() => {
    postDialog.open();
  }, [postDialog]);

  const handleWarn = useCallback(async () => {
    try {
      setWarnLoading(true);
      const rawContent = report.reportedItem!.content || report.reportedItem!.name || '';
      const displayContent = rawContent.length > 50 ? rawContent.slice(0, 50) + '...' : rawContent;

      await createNotification.mutateAsync({
        to: report.reportedItem!.author._id,
        type: 'Ostrzeżenie',
        from: 'Administrator',
        message: `Twoja treść (${report.reportedItemType}) - '${displayContent}' została zgłoszona przez innych użytkowników i narusza nasze wytyczne. Prosimy o zapoznanie się z zasadami i dostosowanie swojej aktywności na platformie, aby uniknąć dalszych konsekwencji.`,
      });

      await updateReportStatus.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
        status: ReportStatus.resolved,
      });

      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });

      toast.success('Ostrzeżenie zostało wysłane do użytkownika');
    } catch (error) {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas wysyłania ostrzeżenia. Spróbuj ponownie później.',
        );
      }
    } finally {
      setWarnLoading(false);
    }
  }, [
    createNotification,
    updateReportStatus,
    report.reportedItemType,
    report.reportedItemId,
    report.reportedItem,
  ]);

  const handleDismiss = useCallback(async () => {
    try {
      setDismissLoading(true);
      await updateReportStatus.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
        status: ReportStatus.dismissed,
      });

      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });

      toast.success('Zgłoszenie zostało ustawione jako odrzucone');
    } catch (error) {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas odrzucania zgłoszenia. Spróbuj ponownie później.',
        );
      }
    } finally {
      setDismissLoading(false);
    }
  }, [updateReportStatus, report.reportedItemId, report.reportedItemType]);

  const handleDeleteContent = useCallback(async () => {
    try {
      setContentDeleteLoading(true);
      const rawContent = report.reportedItem!.content || report.reportedItem!.name || '';
      const displayContent = rawContent.length > 50 ? rawContent.slice(0, 50) + '...' : rawContent;

      await deleteContent.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
      });

      await createNotification.mutateAsync({
        to: report.reportedItem!.author._id,
        type: 'Informacja',
        from: 'Administrator',
        message: `Twoja treść (${report.reportedItemType}) - '${displayContent}' została usunięta z powodu naruszenia wytycznych. Dziękujemy za zrozumienie i zachęcamy do zapoznania się z zasadami platformy.`,
      });

      await updateReportStatus.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
        status: ReportStatus.resolved,
      });

      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });

      toast.success('Treść została pomyślnie usunięta');
    } catch (error) {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania treści. Spróbuj ponownie później.',
        );
      }
    } finally {
      setContentDeleteLoading(false);
    }
  }, [
    report.reportedItemType,
    report.reportedItemId,
    report.reportedItem,
    createNotification,
    updateReportStatus,
    deleteContent,
  ]);

  const handleBlockUser = useCallback(async () => {
    try {
      setBlockLoading(true);

      await deleteContent.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
      });

      await blockUser.mutateAsync(report.reportedItem!.author._id);

      await updateReportStatus.mutateAsync({
        itemId: report.reportedItemId,
        itemType: report.reportedItemType,
        status: ReportStatus.resolved,
      });

      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });

      toast.success('Użytkownik został zablokowany');
    } catch (error) {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas blokowania użytkownika. Spróbuj ponownie później.',
        );
      }
    } finally {
      setBlockLoading(false);
    }
  }, [
    report.reportedItemId,
    report.reportedItemType,
    report.reportedItem,
    blockUser,
    updateReportStatus,
    deleteContent,
  ]);

  const handleRemoveReports = useCallback(() => {
    deleteReport.mutate({
      itemId: report.reportedItemId,
      itemType: report.reportedItemType,
    });
  }, [deleteReport, report.reportedItemId, report.reportedItemType]);

  return (
    <>
      <div className="rounded-md grid grid-cols-1 md:grid-cols-[1fr_0.25fr] gap-4 p-4 shadow-md border bg-card text-card-foreground">
        <div className="flex flex-col gap-4 flex-1">
          <div className="flex flex-1 w-full items-center gap-3 flex-wrap">
            <ReportStatusBadge status={report.status} />
            <Badge className="min-h-7" variant="accent">
              {parsePersonReportedLabel(report.count)}
            </Badge>
            <div className="flex items-center gap-1 flex-wrap">
              <Typography className="text-xs" variant="muted">
                Ostatnie zgłoszenie: {parseTimeAgo(report.lastReportedAt)}
              </Typography>
              {report.reviewedAt && (
                <Typography className="text-xs" variant="muted">
                  •
                </Typography>
              )}
              {report.reviewedAt && (
                <Typography className="text-xs" variant="muted">
                  Rozpatrzono: {parseTimeAgo(report.reviewedAt)}
                </Typography>
              )}
            </div>
          </div>

          <div className="flex flex-col gap-4 bg-accent/50 text-accent-foreground p-4 rounded-xl overflow-hidden">
            <div className="flex items-center justify-between gap-4 flex-wrap">
              <InfoField className="flex-row items-center gap-2" label="Zgłoszono">
                <ItemTypeBadge type={report.reportedItemType} />
              </InfoField>

              {report.reportedItem && (
                <ShowReportedItem
                  itemId={report.reportedItemId}
                  onPostShow={handleShowPost}
                  parentId={report.reportedItem.parentId}
                  parentType={report.reportedItem.parentType}
                  type={report.reportedItemType}
                />
              )}
            </div>

            {report.reportedItem ? (
              <>
                <InfoField className="gap-2" label="Treść">
                  <Typography className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent text-sm text-accent-foreground">
                    {report.reportedItem.content || report.reportedItem.name}
                  </Typography>
                </InfoField>

                <InfoField className="gap-2" label="Autor">
                  <Button asChild className="p-2 w-fit" variant="ghost">
                    <a
                      className="hover:ring-ring/50 hover:ring-[3px]"
                      href={`/profile/${report.reportedItem.author.username}`}
                      rel="noreferrer"
                      target="_blank"
                      title="Zobacz profil autora"
                    >
                      <ReusableAvatar
                        className="cursor-pointer shadow-sm size-8"
                        profilePicture={report.reportedItem.author.profileDetails?.profilePicture}
                        username={report.reportedItem.author.username}
                      />
                      {report.reportedItem.author.username}
                    </a>
                  </Button>

                  {report.reportedItem.author.isBlocked && (
                    <Badge className="w-fit" variant="destructive">
                      <Ban className="mr-1 size-3" />
                      Użytkownik zablokowany
                    </Badge>
                  )}

                  <Typography className="text-xs" variant="muted">
                    {`Dodane: ${new Date(report.reportedItem.createdAt).toLocaleString()}`}
                  </Typography>
                </InfoField>
              </>
            ) : (
              <Typography>Treść została usunięta</Typography>
            )}
          </div>
        </div>

        <ReportGroupActions
          isAuthor={isAuthor}
          isClearReportLoading={deleteReport.isPending}
          isDismissLoading={dismissLoading}
          isLoading={dismissLoading || deleteReport.isPending || warnLoading}
          isPendingStatus={report.status === ReportStatus.pending}
          isReportedItemAvailable={!!report.reportedItem}
          isReportsShown={showReportsDetails}
          isUserBlocked={report.reportedItem?.author.isBlocked}
          isWarnLoading={warnLoading}
          onBlockUser={blockUserDialog.open}
          onDeleteContent={deleteContentDialog.open}
          onDismiss={handleDismiss}
          onRemoveReports={handleRemoveReports}
          onToggleReports={handleToggleReportsDetails}
          onWarn={handleWarn}
        />

        {showReportsDetails && (
          <div className="col-span-full mt-2 grid gap-4">
            {isReportsLoading ? (
              Array.from({ length: 3 }).map((_, i) => <Skeleton className="h-32" key={i} />)
            ) : isReportsError || !reportItems ? (
              <Alert>
                <Info />
                <AlertTitle>Błąd pobierania szczegółów zgłoszeń</AlertTitle>
                <AlertDescription>Spróbuj ponownie później lub odśwież stronę.</AlertDescription>
              </Alert>
            ) : (
              reportItems.map((r) => <Report key={r._id} report={r} />)
            )}
          </div>
        )}
      </div>

      {postDialog.isOpen && (
        <>
          {isPostLoading ? (
            <Dialog open>
              <DialogContent className="flex flex-col items-center justify-center gap-4 max-w-md [&>button]:hidden min-h-48">
                <Loader2 className="animate-spin size-10" />
                <Typography>Ładowanie danych posta...</Typography>
              </DialogContent>
            </Dialog>
          ) : isPostError || !postData ? (
            <Dialog open>
              <DialogContent className="flex flex-col items-center justify-center gap-4 max-w-md [&>button]:hidden min-h-48">
                <Typography>Wystąpił błąd podczas pobierania informacji o poście</Typography>

                <DialogFooter className="w-full">
                  <DialogClose asChild>
                    <Button className="w-2/3 mx-auto" type="button" variant="outline">
                      Powrót
                    </Button>
                  </DialogClose>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          ) : (
            <PostDetails
              onClose={postDialog.close}
              onOpenChange={postDialog.setIsOpen}
              open={postDialog.isOpen}
              post={postData}
            />
          )}
        </>
      )}

      <ReusableAlertDialog
        cancelLabel="Anuluj"
        confirmLabel="Zablokuj użytkownika i usuń treść"
        description="Użytkownik zostanie zablokowany i nie będzie mógł korzystać z platformy. Ta operacja jest nieodwracalna. Dodatkowo treść użytkownika zostanie usunięta."
        isLoading={blockLoading}
        onClose={blockUserDialog.close}
        onConfirm={handleBlockUser}
        open={blockUserDialog.isOpen}
        title={`Blokada użytkownika @${report.reportedItem?.author.username}`}
        variant="destructive"
      />

      <ReusableAlertDialog
        cancelLabel="Anuluj"
        confirmLabel="Usuń treść"
        description="Ta operacja jest nieodwracalna. Autor otrzyma powiadomienie o usunięciu treści. Czy na pewno chcesz kontynuować?"
        isLoading={contentDeleteLoading}
        onClose={deleteContentDialog.close}
        onConfirm={handleDeleteContent}
        open={deleteContentDialog.isOpen}
        title={`Usunięcie treści (${report.reportedItemType})`}
        variant="destructive"
      />
    </>
  );
}

interface ShowReportedItemProps {
  type: ItemType;
  itemId: string;
  parentType?: ItemType;
  parentId?: string;
  onPostShow: () => void;
}

function ShowReportedItem({
  type,
  itemId,
  parentType,
  parentId,
  onPostShow,
}: ShowReportedItemProps) {
  if (type === 'Trening' || type === 'Ćwiczenie') {
    return (
      <Button asChild variant="link">
        <a
          href={type === 'Trening' ? `/workouts/${itemId}` : `/exercises/${itemId}`}
          rel="noreferrer"
          target="_blank"
          title={`Przejdź do ${type.toLowerCase()}`}
        >
          <ExternalLink className="mr-1" />
          Zobacz {type === 'Trening' ? 'zgłoszony plan treningowy' : 'zgłoszone ćwiczenie'}
        </a>
      </Button>
    );
  }

  if (type === 'Post') {
    return (
      <Button onClick={onPostShow} variant="link">
        <ExternalLink className="mr-1" />
        Otwórz zgłoszony post
      </Button>
    );
  }

  if (type === 'Komentarz') {
    if (parentType === 'Ćwiczenie' || parentType === 'Trening') {
      return (
        <Button asChild variant="link">
          <a
            href={parentType === 'Trening' ? `/workouts/${parentId}` : `/exercises/${parentId}`}
            rel="noreferrer"
            target="_blank"
            title={`Przejdź do ${parentType === 'Trening' ? 'planu' : 'ćwiczenia'}`}
          >
            <ExternalLink className="mr-1" />
            Zobacz komentarz pod {parentType === 'Trening' ? 'planem treningowym' : 'ćwiczeniem'}
          </a>
        </Button>
      );
    }

    if (parentType === 'Post') {
      return (
        <Button onClick={onPostShow} variant="link">
          <ExternalLink className="mr-1" />
          Zobacz komentarz pod postem
        </Button>
      );
    }
  }

  return null;
}

interface ReportGroupActionsProps {
  isPendingStatus: boolean;
  isLoading: boolean;
  isClearReportLoading: boolean;
  isWarnLoading: boolean;
  isDismissLoading: boolean;
  isAuthor: boolean;
  isReportsShown: boolean;
  isReportedItemAvailable: boolean;
  isUserBlocked?: boolean;
  onWarn: () => void;
  onDismiss: () => void;
  onDeleteContent: () => void;
  onBlockUser: () => void;
  onRemoveReports: () => void;
  onToggleReports: () => void;
}

function ReportGroupActions({
  isPendingStatus,
  isReportsShown,
  isLoading,
  isClearReportLoading,
  isWarnLoading,
  isDismissLoading,
  isAuthor,
  isUserBlocked,
  isReportedItemAvailable,
  onWarn,
  onDismiss,
  onDeleteContent,
  onBlockUser,
  onRemoveReports,
  onToggleReports,
}: ReportGroupActionsProps) {
  return (
    <div className="flex flex-col gap-2 h-full ">
      {isPendingStatus ? (
        <>
          {!isAuthor && !isUserBlocked && isReportedItemAvailable && (
            <Button disabled={isLoading} onClick={onWarn}>
              {isWarnLoading && <Loader2 className="animate-spin" />}
              {isWarnLoading ? 'Wysyłanie...' : 'Wyślij ostrzeżenie do autora'}
            </Button>
          )}

          <Button
            className={cn((isAuthor || isUserBlocked || !isReportedItemAvailable) && 'mt-11')}
            disabled={isLoading}
            onClick={onDismiss}
            variant="outline"
          >
            {isDismissLoading && <Loader2 className="animate-spin" />}
            {isDismissLoading ? 'Odrzucanie...' : 'Odrzuć zgłoszenie'}
          </Button>

          {!isAuthor && !isUserBlocked && isReportedItemAvailable && (
            <>
              <Separator className="my-2 rounded-md" />
              <Button disabled={isLoading} onClick={onDeleteContent} variant="destructive">
                <Trash2 />
                Usuń treść
              </Button>
              <Button disabled={isLoading} onClick={onBlockUser} variant="destructive">
                <Ban />
                Zablokuj autora i usuń treść
              </Button>
            </>
          )}
        </>
      ) : (
        <Button className="mt-11" disabled={isLoading} onClick={onRemoveReports}>
          {isClearReportLoading ? <Loader2 className="animate-spin" /> : <Eraser />}
          {isClearReportLoading ? 'Usuwanie...' : 'Usuń zgłoszenie z listy'}
        </Button>
      )}
      <Separator className="my-2 rounded-md" />
      <Button className="col-span-full" onClick={onToggleReports} variant="outline">
        <ChevronsUpDown />
        {isReportsShown ? 'Ukryj szczegóły zgłoszenia' : 'Pokaż szczegóły zgłoszenia'}
      </Button>
    </div>
  );
}

export default ReportGroup;
