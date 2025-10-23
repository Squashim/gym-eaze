import { ReportType } from '@/constants/enums/report-type';
import { REPORT } from '@/constants/report';
import { z } from 'zod/v3';

export const reportSchema = z
  .object({
    reportType: z.nativeEnum(ReportType, {
      message: 'Typ zgłoszenia jest wymagany',
    }),
    reason: z.string().optional(),
  })
  .refine(
    (data) => {
      if (data.reportType === ReportType.other) {
        if (!data.reason || data.reason.trim().length === 0) {
          return false;
        }

        const normalizedLength = data.reason.replace(/\r\n|\r/g, '\n').length;
        return normalizedLength <= REPORT.MAX_REASON_LENGTH;
      }

      if (data.reason && data.reason.trim().length > 0) {
        return false;
      }

      return true;
    },
    (data) => {
      if (data.reportType === ReportType.other) {
        if (!data.reason || data.reason.trim().length === 0) {
          return {
            message: "Treść zgłoszenia jest wymagana dla typu 'Inny powód'",
            path: ['reason'],
          };
        }

        const normalizedLength = data.reason.replace(/\r\n|\r/g, '\n').length;
        if (normalizedLength > REPORT.MAX_REASON_LENGTH) {
          return {
            message: `Treść zgłoszenia nie może być dłuższa niż ${REPORT.MAX_REASON_LENGTH} znaków.`,
            path: ['reason'],
          };
        }
      }

      if (data.reportType !== ReportType.other && data.reason && data.reason.trim().length > 0) {
        return {
          message: "Treść zgłoszenia można dodać tylko dla typu 'Inny powód'",
          path: ['reason'],
        };
      }

      return { message: '', path: [] };
    },
  );

export type ReportSchema = z.infer<typeof reportSchema>;
