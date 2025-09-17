import multer, { FileFilterCallback } from 'multer';
import { Request, RequestHandler, ErrorRequestHandler } from 'express';
import { TrainingPlanRequest } from '../types/trainingPlan';

const storage = multer.memoryStorage();

export const uploadTrainingPlanFile = multer({
  storage,
  fileFilter: (_: Request, file: Express.Multer.File, cb: FileFilterCallback) => {
    const allowedImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];

    if (file.fieldname === 'image' && !allowedImageTypes.includes(file.mimetype)) {
      return cb(new Error('Niepoprawny format zdjęcia. Dozwolone formaty: JPG, PNG, WebP'));
    }
    cb(null, true);
  },
});

export const checkFileSizes: RequestHandler = (req, res, next) => {
  try {
    const files = (req as TrainingPlanRequest).files;

    if (files?.image?.[0]) {
      const imageFile = files.image[0];
      const imageSizeLimit = 5 * 1024 * 1024; // 5MB

      if (imageFile.size > imageSizeLimit) {
        return res.status(400).json({
          message: 'Zdjęcie jest zbyt duże (maksymalnie 5MB)',
        });
      }
    }

    next();
  } catch {
    return res.status(500).json({
      message: 'Błąd sprawdzania rozmiaru pliku',
    });
  }
};

export const handleMulterError: ErrorRequestHandler = (err, _, res, next) => {
  if (err instanceof multer.MulterError) {
    return res.status(400).json({ message: 'Błąd przesyłania pliku', error: err.message });
  } else if (err instanceof Error) {
    if (err.message.includes('Niepoprawny format zdjęcia')) {
      return res
        .status(400)
        .json({ message: 'Niepoprawny format zdjęcia. Dozwolone formaty: JPG, PNG, WebP' });
    }
  }
  next();
};
