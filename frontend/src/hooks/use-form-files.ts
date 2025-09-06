import { areFilesEqual, urlToFile } from '@/lib/utils';
import { useEffect, useState } from 'react';

type UseFormFilesProps = {
  imageUrl?: string | null;
  videoUrl?: string | null;
  enabled?: boolean;
};

function useFormFiles({ imageUrl = null, videoUrl = null, enabled = true }: UseFormFilesProps) {
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [videoFile, setVideoFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!enabled) return;

    const loadFiles = async () => {
      setIsLoading(true);
      try {
        const loadedImage = imageUrl ? await urlToFile(imageUrl) : null;
        const loadedVideo = videoUrl ? await urlToFile(videoUrl) : null;
        setImageFile(loadedImage);
        setVideoFile(loadedVideo);
      } catch {
        console.error('Błąd podczas ładowania plików z URL');
        setVideoFile(null);
        setImageFile(null);
      } finally {
        setIsLoading(false);
      }
    };

    void loadFiles();
  }, [imageUrl, videoUrl, enabled]);

  function compareFiles(newImageFile?: File | null, newVideoFile?: File | null) {
    const result: Partial<{ isImageDefault: boolean; isVideoDefault: boolean }> = {};

    if (newImageFile !== undefined && newImageFile !== null) {
      result.isImageDefault = areFilesEqual(imageFile, newImageFile);
    }

    if (newVideoFile !== undefined && newVideoFile !== null) {
      result.isVideoDefault = areFilesEqual(videoFile, newVideoFile);
    }

    return result;
  }

  return { imageFile, videoFile, isLoading, compareFiles };
}

export default useFormFiles;
