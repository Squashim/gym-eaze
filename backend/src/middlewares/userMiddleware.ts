import multer, { FileFilterCallback } from "multer";
import { Request, Response, NextFunction } from "express";

const uploadProfilePicture = multer({
  storage: multer.memoryStorage(),
  fileFilter: (_: Request, file: Express.Multer.File, cb: FileFilterCallback) => {
    const allowedImageTypes = ["image/jpeg", "image/jpg", "image/png", "image/webp"];

    if (!allowedImageTypes.includes(file.mimetype)) {
      return cb(new Error("Niepoprawny format zdjęcia. Dozwolone formaty to: JPG, PNG oraz WebP"));
    }

    cb(null, true);
  },
});

export const checkProfilePictureSize = (req: Request, res: Response, next: NextFunction) => {
  const file = (req as any).file;

  if (file) {
    const imageSizeLimit = 5 * 1024 * 1024; // 5MB
    if (file.size > imageSizeLimit) {
      return res.status(400).json({
        message: "Zdjęcie jest zbyt duże (maksymalnie może mieć 5MB)",
      });
    }
  }

  next();
};

export const handleProfileMulterError = (
  err: any,
  _: Request,
  res: Response,
  next: NextFunction
) => {
  if (err instanceof multer.MulterError) {
    return res.status(400).json({ message: "Błąd przesyłania pliku", error: err.message });
  } else if (err instanceof Error) {
    return res.status(400).json({ message: err.message });
  }
  next();
};

export const uploadProfileImage = uploadProfilePicture.single("profilePicture");
