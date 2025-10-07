export interface UserFollowInfo {
  id: string;
  username: string;
  profilePicture: string | null;
}

export interface UserProfile {
  id: string;
  username: string;
  profilePicture: string | null;
  description: string | null;
  followersCount: number;
  followingCount: number;
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
    id: string;
    title: string;
    description: string;
    image: string;
    awardedAt: Date;
  }>;
  isFollowing?: boolean;
}
