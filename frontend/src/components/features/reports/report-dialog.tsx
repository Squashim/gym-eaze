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
import { reportSchema, type ReportSchema } from '@/schemas/report-schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

interface ReportDialogProps {
  open: boolean;
  itemId: string;
  itemType: ItemType;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
}

function ReportDialog({ open, itemId, itemType, onOpenChange, onClose }: ReportDialogProps) {
  // const { updateComment } = useComment({
  //   parentType: parentType,
  //   parentId: parentId,
  // });

  const form = useForm<ReportSchema>({
    resolver: zodResolver(reportSchema),
    defaultValues: {
      reportType: undefined,
      reason: undefined,
    },
  });

  const reportType = form.watch('reportType');

  // const isReply = useMemo(() => {
  //   return parentType === 'Komentarz';
  // }, [parentType]);

  const onSubmit = (data: ReportSchema) => {
    console.log(data);
    // updateComment.mutate(
    //   { id: comment._id, payload: data },
    //   {
    //     onSuccess: () => {
    //       onClose();
    //     },
    //   },
    // );
  };

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden">
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
              <Button onClick={onClose} type="button" variant="outline">
                Anuluj
              </Button>

              <Button type="submit">Zgłoś {itemType.toLowerCase()}</Button>

              {/* <Button
                disabled={
                  !form.formState.isValid || updateComment.isPending || !form.formState.isDirty
                }
                type="submit"
              >
                {updateComment.isPending ? (
                  <>
                    <Loader2 className="animate-spin" />
                    Edytowanie...
                  </>
                ) : (
                  <>
                    <Pencil />
                    {!isReply ? 'Edytuj komentarz' : 'Edytuj odpowiedź'}
                  </>
                )}
              </Button> */}
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default ReportDialog;
