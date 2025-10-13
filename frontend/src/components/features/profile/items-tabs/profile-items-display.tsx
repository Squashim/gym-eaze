import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ItemType } from '@/constants/enums/item-type';
import { profileItemsTabsMapping } from '@/constants/mappings/user-settings';
import { useState } from 'react';
import PostsTab from './posts-tab';
import { Card } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import Typography from '@/components/ui/typography';

export type ProfileItemsTabs = Exclude<ItemType, 'Komentarz'>;

interface ProfileItemsDisplayProps {
  profileId?: string;
  isOwnProfile: boolean;
  isProfileLoading: boolean;
  isProfileError: boolean;
}

function ProfileItemsDisplay({
  isOwnProfile,
  profileId,
  isProfileError,
  isProfileLoading,
}: ProfileItemsDisplayProps) {
  const [selectedTab, setSelectedTab] = useState<ProfileItemsTabs>(ItemType.Post);

  const handleTabChange = (tab: string) => {
    setSelectedTab(tab as ProfileItemsTabs);
  };

  if (isProfileLoading) {
    return (
      <Card className="w-full h-full flex flex-col gap-4 p-4">
        <div className="flex items-center w-full justify-between gap-4">
          {Array.from({ length: 3 }).map((_, index) => (
            <Skeleton className="h-10 w-full" key={index} />
          ))}
        </div>
        <Skeleton className="h-full w-full" />
      </Card>
    );
  }

  if (isProfileError || !profileId) {
    return (
      <Card className="w-full h-full p-4 flex items-center justify-center">
        <Typography className="text-center text-balance" variant="h3">
          Wystąpił błąd podczas ładowania profilu. Odśwież stronę lub spróbuj ponownie później.
        </Typography>
      </Card>
    );
  }

  return (
    <Tabs
      className="grid w-full grid-cols-1 gap-1 h-full mt-6 md:mt-0"
      defaultValue={selectedTab}
      onValueChange={handleTabChange}
    >
      <div className="relative overflow-x-auto h-15 pt-2 scrollbar w-full">
        <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2 bg-transparent">
          {profileItemsTabsMapping.map((tab) => (
            <TabsTrigger
              className="h-10 px-4 data-[state=active]:bg-card/70 dark:data-[state=active]:bg-card/70 bg-card/70 hover:border-input"
              key={tab.value}
              value={tab.value}
            >
              <tab.icon />
              {tab.label}
            </TabsTrigger>
          ))}
        </TabsList>
      </div>

      <TabsContent value={ItemType.Post}>
        <PostsTab isOwnProfile={isOwnProfile} profileId={profileId} />
      </TabsContent>
      <TabsContent value={ItemType.Exercise}></TabsContent>
      <TabsContent value={ItemType.Training}></TabsContent>
    </Tabs>
  );
}

export default ProfileItemsDisplay;
