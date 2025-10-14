import { createReportService } from '../services/reportService';
import { AuthRequest } from '../types/auth';
import { Response } from 'express';

export const createReport = async (req: AuthRequest, res: Response) => {
  const userId = req.userId;

  try {
    const { reportedItemId, reportedItemType, reportType, reason } = req.body;

    const trimmedReason = reason ? reason.trim() : undefined;

    const report = await createReportService(
      {
        reportedItemId,
        reportedItemType,
        reportType,
        reason: trimmedReason,
      },
      userId!,
    );

    return res.status(201).json(report);
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }

    return res.status(500).json({
      message: 'Błąd dodawania zgłoszenia',
    });
  }
};
