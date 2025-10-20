import { create, deleteReport, updateStatus } from '@/api/report';
import queryClient from '@/config/query-client';
import type { ItemType } from '@/constants/enums/item-type';
import { getApiErrorMsg, isReportsQuery } from '@/lib/utils';
import type { CreateReportPayload, UpdateReportStatusPayload } from '@/types/report';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';

export function useReport() {
  const createMutation = useMutation({
    mutationFn: (payload: CreateReportPayload) => create(payload),
    onSuccess: (response) => {
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas wysyłania zgłoszenia. Spróbuj ponownie później.',
        );
      }
    },
  });

  const updateStatusMutation = useMutation({
    mutationFn: (payload: UpdateReportStatusPayload) => updateStatus(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });
      toast.success('Zgłoszenie zostało ustawione jako odrzucone');
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas wysyłania zgłoszenia. Spróbuj ponownie później.',
        );
      }
    },
  });

  const deleteReportMutation = useMutation({
    mutationFn: ({ itemId, itemType }: { itemId: string; itemType: ItemType }) =>
      deleteReport(itemId, itemType),
    onSuccess: async (response) => {
      await queryClient.invalidateQueries({
        predicate: (query) => isReportsQuery(query.queryKey),
      });
      toast.success(response.message);
    },
    onError: (error) => {
      const errorMsg = getApiErrorMsg(error);

      if (errorMsg) {
        toast.error(errorMsg);
      } else {
        toast.error(
          'Wystąpił nieoczekiwany błąd podczas usuwania zgłoszenia. Spróbuj ponownie później.',
        );
      }
    },
  });

  return {
    createReport: createMutation,
    upedateReportStatus: updateStatusMutation,
    deleteReport: deleteReportMutation,
  };
}
