import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import Typography from '@/components/ui/typography';
import type { ItemType } from '@/constants/enums/item-type';
import { ReportType } from '@/constants/enums/report-type';
import { REPORT } from '@/constants/report';
import { useReport } from '@/hooks/query/reports/use-report';
import { reportSchema, type ReportSchema } from '@/schemas/report-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { Loader2 } from 'lucide-react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';

interface ReportDialogProps {
  open: boolean;
  itemId: string;
  itemType: ItemType;
  isReply?: boolean;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

function ReportDialog({
  open,
  itemId,
  itemType,
  isReply = false,
  onOpenChange,
  onClose,
}: ReportDialogProps) {
  const { createReport } = useReport();

  const form = useForm<ReportSchema>({
    resolver: zodResolver(reportSchema),
    defaultValues: {
      reportType: '' as ReportType,
      reason: '',
    },
  });

  const reportType = form.watch('reportType');

  useEffect(() => {
    if (open) {
      form.reset({
        reportType: '' as ReportType,
        reason: '',
      });
    }
  }, [open, form]);

  const onSubmit = (data: ReportSchema) => {
    createReport.mutate(
      {
        reportedItemId: itemId,
        reportedItemType: itemType,
        reportType: data.reportType,
        reason: data.reason,
      },
      {
        onSuccess: () => {
          form.reset();
          onClose();
        },
      },
    );
  };

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden" onInteractOutside={(e) => e.preventDefault()}>
        <DialogHeader>
          <DialogTitle>Zgłoś treść</DialogTitle>
          <DialogDescription>
            Pomóż nam utrzymać bezpieczną społeczność. Wybierz odpowiedni powód zgłoszenia.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form className="grid gap-4" onSubmit={form.handleSubmit(onSubmit)}>
            <FormField
              control={form.control}
              name="reportType"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="font-semibold" htmlFor={field.name}>
                    Jaki jest powód zgłoszenia?
                  </FormLabel>
                  <FormControl>
                    <Select
                      name={field.name}
                      onValueChange={(value) => value && field.onChange(value)}
                      value={field.value}
                    >
                      <SelectTrigger
                        aria-invalid={!!form.formState.errors.reportType}
                        className="w-full"
                        id={field.name}
                      >
                        <SelectValue placeholder="Wybierz powód..." />
                      </SelectTrigger>
                      <SelectContent>
                        {Object.entries(ReportType).map(([key, value]) => (
                          <SelectItem key={key} value={value}>
                            {value}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {reportType === ReportType.other && (
              <FormField
                control={form.control}
                name="reason"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="font-semibold" htmlFor={field.name}>
                      Opisz szczegóły problemu
                    </FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        autoComplete="off"
                        className="resize-none dark:bg-card bg-card max-h-24 scrollbar overflow-y-auto break-words"
                        id={field.name}
                        maxLength={REPORT.MAX_REASON_LENGTH}
                        placeholder="Wyjaśnij dokładnie, na czym polega problem..."
                      />
                    </FormControl>
                    <div className="flex justify-between items-center min-h-5">
                      <FormMessage />
                      <Typography className="text-xs text-muted-foreground ml-auto" variant="small">
                        {field.value?.length ?? 0}/{REPORT.MAX_REASON_LENGTH}
                      </Typography>
                    </div>
                  </FormItem>
                )}
              />
            )}

            <div className="ml-auto flex items-center gap-2">
              <Button
                disabled={createReport.isPending}
                onClick={onClose}
                type="button"
                variant="outline"
              >
                Anuluj
              </Button>

              <Button disabled={createReport.isPending} type="submit">
                {createReport.isPending ? (
                  <>
                    <Loader2 className="animate-spin" />
                    <span>Wysyłanie...</span>
                  </>
                ) : (
                  <span>Zgłoś {isReply ? 'odpowiedź' : itemType.toLowerCase()}</span>
                )}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default ReportDialog;
