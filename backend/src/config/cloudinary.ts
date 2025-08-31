import { v2 as cloudinary } from "cloudinary";
import { Readable } from "stream";
import dotenv from "dotenv";
dotenv.config();

cloudinary.config({
  cloud_name: process.env.CLOUDINARY_NAME,
  api_key: process.env.CLOUDINARY_KEY,
  api_secret: process.env.CLOUDINARY_SECRET,
});

export const uploadBufferToCloudinary = (
  buffer: Buffer, 
  folder: string, 
  resource_type: "image" | "video" = "image"
): Promise<string> => {
  return new Promise<string>((resolve, reject) => {
    const stream = cloudinary.uploader.upload_stream(
      { folder, resource_type },
      (error, result) => {
        if (error) return reject(error);
        resolve(result!.secure_url);
      }
    );
    Readable.from(buffer).pipe(stream);
  });
};

export const uploadExerciseMedia = async (
  imageBuffer?: Buffer, 
  videoBuffer?: Buffer
): Promise<{ imageUrl?: string; videoUrl?: string }> => {
  const uploadPromises: Promise<string>[] = [];
  const results: { imageUrl?: string; videoUrl?: string } = {};

  if (imageBuffer) {
    uploadPromises.push(
      uploadBufferToCloudinary(imageBuffer, "exercises/images", "image")
        .then(url => {
          results.imageUrl = url;
          return url;
        })
    );
  }

  if (videoBuffer) {
    uploadPromises.push(
      uploadBufferToCloudinary(videoBuffer, "exercises/videos", "video")
        .then(url => {
          results.videoUrl = url;
          return url;
        })
    );
  }

  await Promise.all(uploadPromises);
  return results;
};

export default cloudinary;
