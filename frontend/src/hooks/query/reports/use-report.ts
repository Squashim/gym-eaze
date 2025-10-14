import { create } from '@/api/report';
import { getApiErrorMsg } from '@/lib/utils';
import type { CreateReportPayload } from '@/types/report';
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

  return {
    createReport: createMutation,
  };
}
