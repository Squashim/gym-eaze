import FitnessDetailsTab from '@/components/features/profile/edit-tabs/fitness-details';
import LoginDetailsTab from '@/components/features/profile/edit-tabs/login-details';
import PersonalDetailsTab from '@/components/features/profile/edit-tabs/personal-details';
import ProfileDetailsTab from '@/components/features/profile/edit-tabs/profile-details';
import PageLayout from '@/components/layout/page';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { profileEditTabsMapping } from '@/constants/mappings/user-settings';
import usePageTitle from '@/hooks/utils/use-page-title';
import { useState } from 'react';

export type ProfileEditTabs =
  | 'profile-details'
  | 'auth-details'
  | 'personal-details'
  | 'fitness-details';

function ProfileEdit() {
  const [selectedTab, setSelectedTab] = useState('profile-details');
  usePageTitle('Zarządzaj kontem');

  const handleTabChange = (tab: string) => {
    setSelectedTab(tab as ProfileEditTabs);
  };

  return (
    <PageLayout
      className="mx-auto w-auto"
      description="Edytuj ustawienia swojego konta"
      title="Zarządzaj kontem"
      withFooter={false}
    >
      <Tabs
        className="grid w-full grid-cols-1 gap-4 max-w-4xl lg:min-w-4xl"
        defaultValue={selectedTab}
        onValueChange={handleTabChange}
      >
        <div className="relative overflow-x-auto h-15 pt-2 scrollbar">
          <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2 bg-transparent">
            {profileEditTabsMapping.map((tab) => (
              <TabsTrigger
                className="h-10 px-4 data-[state=active]:bg-card/70 dark:data-[state=active]:bg-card/70 bg-card/70 hover:border-input"
                key={tab.value}
                value={tab.value}
              >
                {tab.label}
              </TabsTrigger>
            ))}
          </TabsList>
        </div>

        <TabsContent value="profile-details">
          <ProfileDetailsTab />
        </TabsContent>
        <TabsContent value="auth-details">
          <LoginDetailsTab />
        </TabsContent>
        <TabsContent value="personal-details">
          <PersonalDetailsTab />
        </TabsContent>
        <TabsContent value="fitness-details">
          <FitnessDetailsTab />
        </TabsContent>
      </Tabs>
    </PageLayout>
  );
}

export default ProfileEdit;
