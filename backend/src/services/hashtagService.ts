import Hashtag, { IHashtag } from "../models/HashtagModel";

export const getAllHashtags = async (): Promise<IHashtag[]> => {
  return await Hashtag.find().sort({ name: 1 });
};

export const getPopularHashtags = async (limit: number = 20): Promise<IHashtag[]> => {
  return await Hashtag.find()
    .sort({ usageCount: -1, name: 1 })
    .limit(limit);
};

export const findOrCreateHashtags = async (hashtagNames: string[]): Promise<IHashtag[]> => {
  const hashtags: IHashtag[] = [];
  
  for (const name of hashtagNames) {
    if (!name || name.trim().length < 2 || name.trim().length > 30) {
      continue;
    }
    
    const trimmedName = name.trim().toLowerCase();
    
    let hashtag = await Hashtag.findOne({ name: trimmedName });
    
    if (!hashtag) {
      hashtag = new Hashtag({ name: trimmedName });
      await hashtag.save();
    }
    
    hashtags.push(hashtag);
  }
  
  return hashtags;
};

export const incrementUsageCount = async (hashtagIds: string[]): Promise<void> => {
  await Hashtag.updateMany(
    { _id: { $in: hashtagIds } },
    { $inc: { usageCount: 1 } }
  );
};