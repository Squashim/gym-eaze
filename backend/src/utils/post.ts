import { FilterQuery } from 'mongoose';
import { PostFilters } from '../types/post';
import Post, { IPost } from '../models/postModel';
import User from '../models/userModel';

export const getPostsWithCursor = async (
  filters: PostFilters,
  cursor?: string,
  limit: number = 10,
  userId?: string,
) => {
  const query: FilterQuery<IPost> = {};
  const andFilters: FilterQuery<IPost>[] = [];

  if (cursor) {
    andFilters.push({ _id: { $lt: cursor } });
  }

  if (filters.hashtags?.length) {
    andFilters.push({ hashtags: { $in: filters.hashtags } });
  }

  if (filters.search?.trim()) {
    andFilters.push({
      $or: [
        { content: { $regex: filters.search.trim(), $options: 'i' } },
        { hashtags: { $in: [new RegExp(filters.search.trim(), 'i')] } },
      ],
    });
  }

  if (filters.followedOnly && userId) {
    const user = await User.findById(userId).select('followedUsers').lean();
    const followedUsers = user?.followedUsers ?? [];

    if (followedUsers.length > 0) {
      andFilters.push({ author: { $in: followedUsers } });
    } else {
      return { items: [], hasMore: false, nextCursor: null };
    }
  }

  if (andFilters.length) {
    query.$and = andFilters;
  }

  const items = await Post.find(query)
    .sort({ createdAt: -1, _id: -1 })
    .limit(limit + 1)
    .populate('author', 'username profileDetails.profilePicture')
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
