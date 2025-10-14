export type BadgeApiResponse = {
  id: string;
  title: string;
  description: string;
  image: string;
};

export type ProfileBadge = {
  _id: string;
  title: string;
  description: string;
  image: string;
  awardedAt: Date;
};
