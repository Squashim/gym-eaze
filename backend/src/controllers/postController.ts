import { Response } from 'express';
import {
  createPost,
  deletePostService,
  getUserPostsWithCursor,
  togglePostLikeService,
  updatePostService,
} from '../services/postService';
import { AuthPostRequest, PostFilters } from '../types/post';
import { parseField } from '../utils/parsers';
import { AuthRequest } from '../types/auth';
import { getPostsWithCursor } from '../utils/post';

export const addPost = async (req: AuthPostRequest, res: Response) => {
  try {
    const { content, hashtags } = req.body;
    const userId = req.userId;

    const imageFile = req.files?.image?.[0];
    const videoFile = req.files?.video?.[0];

    if (!content || content.trim().length === 0) {
      return res.status(400).json({ message: 'Treść posta jest wymagana' });
    }
    if (content.length > 800) {
      return res.status(400).json({ message: 'Treść posta może mieć maksymalnie 800 znaków' });
    }

    if (imageFile && videoFile) {
      return res.status(400).json({
        message: 'Nie możesz dodać jednocześnie zdjęcia i filmiku. Wybierz tylko jedno.',
      });
    }

    if ((imageFile || videoFile) && content.trim().length === 0) {
      return res.status(400).json({
        message: 'Nie możesz dodać samego zdjęcia lub filmiku. Post musi zawierać treść.',
      });
    }

    const hashtagsArray = parseField(hashtags);

    await createPost(
      {
        author: userId!,
        content: content.trim(),
        hashtags: hashtagsArray,
      },
      imageFile?.buffer,
      videoFile?.buffer,
    );

    return res.status(201).json({ message: 'Post został dodany' });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd podczas dodawania posta' });
  }
};

export const updatePost = async (req: AuthPostRequest, res: Response) => {
  try {
    const postId = req.params.id;
    const userId = req.userId;

    const content = req.body.content;
    const imageFile = req.files?.image?.[0];
    const videoFile = req.files?.video?.[0];

    await updatePostService(postId!, userId!, {
      content,
      imageBuffer: imageFile?.buffer,
      videoBuffer: videoFile?.buffer,
    });

    return res.status(200).json({ message: 'Post został zaktualizowany' });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd podczas aktualizacji posta' });
  }
};

export const deletePost = async (req: AuthPostRequest, res: Response) => {
  try {
    const postId = req.params.id;
    const userId = req.userId;

    await deletePostService(postId!, userId!);

    return res.status(200).json({ message: 'Post został usunięty' });
  } catch (error) {
    if (error instanceof Error) {
      return res.status(400).json({ message: error.message });
    }
    return res.status(500).json({ message: 'Błąd podczas usuwania posta' });
  }
};

export const togglePostLike = async (req: AuthPostRequest, res: Response) => {
  try {
    const userId = req.userId;
    const postId = req.params.id;

    if (!userId) return res.status(401).json({ message: 'Brak autoryzacji użytkownika' });

    const result = await togglePostLikeService(userId, postId);

    res.status(200).json(result);
  } catch (error) {
    if (error instanceof Error) {
      res.status(400).json({ message: error.message });
    } else {
      res.status(500).json({ message: 'Błąd serwera' });
    }
  }
};

export const getPosts = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    const { hashtags, followedOnly, cursor, limit = 10 } = req.query;

    let parsedHashtags: string[] | undefined;
    if (hashtags) {
      if (Array.isArray(hashtags)) {
        parsedHashtags = hashtags.map((tag) => String(tag));
      } else {
        parsedHashtags = [String(hashtags)];
      }
    }

    if (followedOnly === 'true' && !userId) {
      return res.status(401).json({
        message: 'Musisz się zalogować, aby zobaczyć posty obserwowanych użytkowników.',
      });
    }

    const filters: PostFilters = {
      hashtags: parsedHashtags,
      followedOnly: userId ? followedOnly === 'true' : false,
    };

    const posts = await getPostsWithCursor(filters, cursor as string, Number(limit), userId);

    return res.json(posts);
  } catch (error) {
    return res.status(500).json({
      message: error instanceof Error ? error.message : 'Błąd serwera',
    });
  }
};

export const getPostsByUser = async (req: AuthRequest, res: Response) => {
  try {
    const { userId } = req.params;
    const { cursor, limit = 10 } = req.query;

    const result = await getUserPostsWithCursor(userId, cursor as string, Number(limit));

    return res.json({
      ...result,
    });
  } catch (error) {
    return res.status(500).json({
      message: error instanceof Error ? error.message : 'Błąd serwera',
    });
  }
};
