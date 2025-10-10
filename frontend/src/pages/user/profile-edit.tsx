import FitnessDetailsTab from '@/components/features/profile/edit-tabs/fitness-details';
import LoginDetailsTab from '@/components/features/profile/edit-tabs/login-details';
import PersonalDetailsTab from '@/components/features/profile/edit-tabs/personal-details';
import ProfileDetailsTab from '@/components/features/profile/edit-tabs/profile-details';
import PageLayout from '@/components/layout/page';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useState } from 'react';

type ProfileEditTabs = 'profile-details' | 'auth-details' | 'personal-details' | 'fitness-details';

const PROFILE_EDIT_TABS: { label: string; value: ProfileEditTabs }[] = [
  {
    label: 'Informacje o profilu',
    value: 'profile-details',
  },
  {
    label: 'Dane logowania',
    value: 'auth-details',
  },
  {
    label: 'Dane personalne',
    value: 'personal-details',
  },
  {
    label: 'Informacje fizyczne',
    value: 'fitness-details',
  },
];

function ProfileEdit() {
  const [selectedTab, setSelectedTab] = useState('profile-details');

  const handleTabChange = (tab: string) => {
    setSelectedTab(tab as ProfileEditTabs);
  };

  return (
    <PageLayout description="Edytuj ustawienia swojego konta" title="Zarządzaj kontem">
      <Tabs
        className="flex-1 min-h-0 max-h-full h-full"
        defaultValue={selectedTab}
        onValueChange={handleTabChange}
      >
        <div className="relative overflow-x-auto h-16 pt-2 scrollbar">
          <TabsList className="absolute flex flex-row justify-stretch w-full py-2 gap-2 bg-transparent">
            {PROFILE_EDIT_TABS.map((tab) => (
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
