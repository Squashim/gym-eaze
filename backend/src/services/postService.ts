import mongoose, { FilterQuery, SortOrder, Types } from 'mongoose';
import { deleteFromCloudinary, uploadBufferToCloudinary } from '../config/cloudinary';
import Post, { IPost } from '../models/postModel';
import { CreatePostData, PostFilters, UpdatePostData } from '../types/post';
import User from '../models/userModel';
import { parseDescription } from '../utils/parsers';

export const createPost = async (
  data: CreatePostData,
  imageBuffer?: Buffer,
  videoBuffer?: Buffer,
): Promise<IPost> => {
  let imageUrl: string | undefined;
  let videoUrl: string | undefined;

  if (data.hashtags) {
    if (data.hashtags.length > 50) {
      throw new Error(`Możesz dodać maksymalnie 50 hashtagów`);
    }

    for (const tag of data.hashtags) {
      if (tag.length > 35) {
        throw new Error(`Hashtag "${tag}" przekracza maksymalną długość 35 znaków`);
      }
      if (!/^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$/.test(tag)) {
        throw new Error(
          `Hashtag "${tag}" zawiera niedozwolone znaki. Dozwolone są tylko litery i cyfry, bez # i spacji.`,
        );
      }
    }
  }

  if (imageBuffer) {
    imageUrl = await uploadBufferToCloudinary(imageBuffer, 'posts/images', 'image');
  }

  if (videoBuffer) {
    videoUrl = await uploadBufferToCloudinary(videoBuffer, 'posts/videos', 'video');
  }

  const post = new Post({
    ...data,
    image: imageUrl,
    video: videoUrl,
  });

  return await post.save();
};

export const updatePostService = async (
  postId: string,
  userId: string,
  data: UpdatePostData,
  image: null | undefined,
  video: null | undefined,
): Promise<void> => {
  const post = await Post.findById(postId);
  if (!post) throw new Error('Post nie istnieje');

  if (post.author.toString() !== userId) {
    throw new Error('Nie masz uprawnień do edycji tego posta');
  }

  const { content, imageBuffer, videoBuffer, hashtags } = data;

  if (content && parseDescription(content).length > 800) {
    throw new Error('Treść posta może mieć maksymalnie 800 znaków');
  }

  if (hashtags && hashtags.length > 0) {
    if (hashtags.length > 50) {
      throw new Error(`Możesz dodać maksymalnie 50 hashtagów`);
    }

    for (const tag of hashtags) {
      if (tag.length > 35) {
        throw new Error(`Hashtag "${tag}" przekracza maksymalną długość 35 znaków`);
      }
      if (!/^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$/.test(tag)) {
        throw new Error(
          `Hashtag "${tag}" zawiera niedozwolone znaki. Dozwolone są tylko litery i cyfry, bez # i spacji.`,
        );
      }
    }

    post.hashtags = hashtags;
  }

  if ((image === null || imageBuffer) && post.image) {
    await deleteFromCloudinary(post.image);
    post.image = undefined;
  }

  if ((video === null || videoBuffer) && post.video) {
    await deleteFromCloudinary(post.video, 'video');
    post.video = undefined;
  }

  if (imageBuffer && videoBuffer) {
    throw new Error('Nie możesz dodać jednocześnie zdjęcia i filmiku. Wybierz tylko jedno.');
  }

  if (imageBuffer) {
    post.image = await uploadBufferToCloudinary(imageBuffer, 'posts/images', 'image');
    post.video = undefined;
  }
  if (videoBuffer) {
    post.video = await uploadBufferToCloudinary(videoBuffer, 'posts/videos', 'video');
    post.image = undefined;
  }

  if (content) {
    post.content = content;
  }

  post.isEdited = true;

  await post.save();
};

export const deletePostService = async (postId: string, userId: string): Promise<void> => {
  const post = await Post.findById(postId);

  if (!post) {
    throw new Error('Post nie istnieje');
  }

  if (post.author.toString() !== userId) {
    throw new Error('Nie masz uprawnień do usunięcia tego posta');
  }

  if (post.image) {
    await deleteFromCloudinary(post.image);
  }

  if (post.video) {
    await deleteFromCloudinary(post.video, 'video');
  }

  await post.deleteOne();
};

export const togglePostLikeService = async (userId: string, postId: string) => {
  if (!mongoose.Types.ObjectId.isValid(postId)) {
    throw new Error('Nieprawidłowe ID posta');
  }

  const user = await User.findById(userId);
  if (!user) throw new Error('Nie znaleziono użytkownika');

  const post = await Post.findById(postId);
  if (!post) throw new Error('Nie znaleziono posta');

  const isLiked = user.likes.posts.some((fav) => fav.toString() === postId);

  if (isLiked) {
    user.likes.posts = user.likes.posts.filter((fav) => fav.toString() !== postId);
    post.likes = Math.max(0, (post.likes || 0) - 1);
    await user.save();
    await post.save();
    return { message: 'Usunięto polubienie', likes: post.likes };
  } else {
    user.likes.posts.push(post._id as Types.ObjectId);
    post.likes = (post.likes || 0) + 1;
    await user.save();
    await post.save();
    return { message: 'Dodano polubienie', likes: post.likes };
  }
};

export const getPostsService = async (
  filters: PostFilters,
  page: number,
  pageSize: number,
  userId?: string,
) => {
  const query: FilterQuery<IPost> = {};
  const andFilters: FilterQuery<IPost>[] = [];

  if (filters.hashtags?.length) {
    andFilters.push({ hashtags: { $in: filters.hashtags } });
  }

  if (filters.followedOnly) {
    if (!userId) {
      return { items: [], total: 0, page, pageSize, hasMore: false };
    }
    const user = await User.findById(userId).select('followedUsers').lean();
    const followedUsers = user?.followedUsers ?? [];
    if (followedUsers.length > 0) {
      andFilters.push({ author: { $in: followedUsers } });
    } else {
      return { items: [], total: 0, page, pageSize, hasMore: false };
    }
  }

  if (andFilters.length) {
    query.$and = andFilters;
  }

  const sort: { [key: string]: SortOrder } = { createdAt: -1, _id: -1 };
  const skip = (page - 1) * pageSize;

  const [items, total] = await Promise.all([
    Post.find(query)
      .sort(sort)
      .skip(skip)
      .limit(pageSize + 1)
      .populate({
        path: 'author',
        select: 'username profileDetails.profilePicture',
        model: 'User',
      })
      .lean(),
    Post.countDocuments(query),
  ]);

  const hasMore = items.length > pageSize;
  if (hasMore) items.pop();

  return {
    items: items.slice(0, pageSize),
    total,
    page,
    pageSize,
    hasMore,
    nextPage: hasMore ? page + 1 : null,
  };
};

export const getUserPostsWithCursor = async (
  userId: string,
  cursor?: string,
  limit: number = 10,
) => {
  const query: FilterQuery<IPost> = { author: userId };
  if (cursor) {
    query._id = { $lt: cursor };
  }

  const items = await Post.find(query)
    .sort({ createdAt: -1, _id: -1 })
    .limit(limit + 1)
    .populate({
      path: 'author',
      select: 'username profileDetails.profilePicture',
      model: 'User',
    })
    .lean();

  const hasMore = items.length > limit;
  if (hasMore) {
    items.pop();
  }

  const nextCursor = hasMore && items.length > 0 ? items[items.length - 1]._id.toString() : null;

  return {
    items: items.slice(0, limit),
    hasMore,
    nextCursor,
  };
};
