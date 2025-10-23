export interface UserProfile {
  _id: string;
  username: string;
  profilePicture: string | null;
  description: string | null;
  followersCount: number;
  followingCount: number;
  isBlocked?: boolean | null;
  goal?: string | null;
  personalDetails?: {
    firstName: string | null;
    lastName: string | null;
    country: string | null;
  };
  fitnessDetails?: {
    height: number | null;
    weight: number | null;
    age: number | null;
    gender: string | null;
  };
  pinnedBadges?: Array<{
    _id: string;
    title: string;
    description: string;
    image: string;
    awardedAt: Date;
  }>;
  isFollowing?: boolean;
}
