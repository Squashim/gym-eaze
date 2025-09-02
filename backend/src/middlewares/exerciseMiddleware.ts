import multer, { FileFilterCallback } from 'multer';
import { Request, RequestHandler, ErrorRequestHandler } from 'express';
import { ExerciseRequest } from '../types/exercise';

const storage = multer.memoryStorage();

export const uploadExerciseFiles = multer({
  storage,
  fileFilter: (_: Request, file: Express.Multer.File, cb: FileFilterCallback) => {
    const allowedImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
    const allowedVideoTypes = ['video/mp4', 'video/webm'];

    if (file.fieldname === 'image' && !allowedImageTypes.includes(file.mimetype)) {
      return cb(new Error('Niepoprawny format zdjęcia. Dozwolone formaty: JPG, PNG, WebP'));
    }

    if (file.fieldname === 'video' && !allowedVideoTypes.includes(file.mimetype)) {
      return cb(new Error('Niepoprawny format wideo. Dozwolone formaty: MP4, WebM'));
    }

    cb(null, true);
  },
});

export const checkFileSizes: RequestHandler = (req, res, next) => {
  try {
    const files = (req as ExerciseRequest).files;

    if (files?.image?.[0]) {
      const imageFile = files.image[0];
      const imageSizeLimit = 5 * 1024 * 1024; // 5MB

      if (imageFile.size > imageSizeLimit) {
        return res.status(400).json({
          message: 'Zdjęcie jest zbyt duże (maksymalnie 5MB)',
        });
      }
    }

    if (files?.video?.[0]) {
      const videoFile = files.video[0];
      const videoSizeLimit = 20 * 1024 * 1024; // 20MB

      if (videoFile.size > videoSizeLimit) {
        return res.status(400).json({
          message: 'Filmik jest za duży(maksymalnie 20MB)',
        });
      }
    }

    next();
  } catch {
    return res.status(500).json({
      message: 'Błąd sprawdzania rozmiaru plików',
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
    } else if (err.message.includes('Niepoprawny format filmiku')) {
      return res
        .status(400)
        .json({ message: 'Niepoprawny format filmiku. Dozwolone formaty: MP4, WebM' });
    }
    return res.status(400).json({ message: err.message });
  }
  next();
};
