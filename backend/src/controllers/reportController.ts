import {
  createReportService,
  deleteReportsForItemService,
  getAllReportsService,
  getReportsForItemService,
  updateReportStatusForItemService,
} from '../services/reportService';
import { AuthRequest } from '../types/auth';
import { Response } from 'express';
import { ReportQuery } from '../types/report';

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

export const getAllReports = async (req: AuthRequest, res: Response) => {
  const userId = req.userId;
  try {
    const { filters = {}, page = 1, pageSize = 9 }: ReportQuery = req.body;
    const reports = await getAllReportsService(filters, page, pageSize, userId!);

    return res.json(reports);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
      } else if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: 'Brak uprawnień' });
      }

      return res.status(400).json({ message: error.message });
    } else {
      return res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getReportsForItem = async (req: AuthRequest, res: Response) => {
  const userId = req.userId;

  try {
    const { reportedItemId, reportedItemType } = req.params;

    const reports = await getReportsForItemService(userId!, reportedItemId, reportedItemType);

    return res.json(reports);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
      } else if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: 'Brak uprawnień' });
      }
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const updateReportStatusForItem = async (req: AuthRequest, res: Response) => {
  const userId = req.userId;
  const { reportedItemId, reportedItemType } = req.params;
  const { status } = req.body;

  try {
    const result = await updateReportStatusForItemService(
      userId!,
      reportedItemId,
      reportedItemType,
      status,
    );

    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
      } else if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: 'Brak uprawnień' });
      } else if (error.message === 'REPORTS_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono zgłoszeń dla podanego podmiotu' });
      }
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd serwera' });
  }
};

export const deleteReportsForItem = async (req: AuthRequest, res: Response) => {
  const userId = req.userId;
  const { reportedItemId, reportedItemType } = req.params;

  try {
    const result = await deleteReportsForItemService(userId!, reportedItemId, reportedItemType);

    return res.json(result);
  } catch (error) {
    if (error instanceof Error) {
      if (error.message === 'USER_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono użytkownika' });
      } else if (error.message === 'NOT_ADMIN') {
        return res.status(403).json({ message: 'Brak uprawnień' });
      } else if (error.message === 'REPORTS_NOT_FOUND') {
        return res.status(404).json({ message: 'Nie znaleziono zgłoszeń do usunięcia' });
      }
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd serwera' });
  }
};
