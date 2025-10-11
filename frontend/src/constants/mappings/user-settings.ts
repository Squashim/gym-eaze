import type { ProfileEditTabs } from '@/pages/user/profile-edit';
import type { UserPrivacySettings } from '@/types/user';

export const profileEditTabsMapping: { label: string; value: ProfileEditTabs }[] = [
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

export const privacySettingsMapping: {
  label: string;
  description: string;
  name: keyof UserPrivacySettings['data'];
}[] = [
  {
    label: 'Dane personalne',
    description:
      'Określa, czy Twoje dane personalne takie jak imię, nazwisko i kraj są widoczne dla innych użytkowników.',
    name: 'isPersonalDetailsPublic',
  },
  {
    label: 'Informacje fizyczne',
    description:
      'Określa, czy Twoje informacje fizyczne takie jak wiek, wzrost, waga i płeć są widoczne dla innych użytkowników.',
    name: 'isFitnessDetailsPublic',
  },
  {
    label: 'Cel treningowy',
    description: 'Określa, czy Twój cel treningowy jest widoczny dla innych użytkowników.',
    name: 'isGoalPublic',
  },
  {
    label: 'Przypięte odznaki',
    description:
      'Określa, czy przypięte odznaki na Twoim profilu są widoczne dla innych użytkowników.',
    name: 'isBadgesDetailsPublic',
  },
  {
    label: 'Statystyki treningów',
    description: 'Określa, czy inni użytkownicy mogą zobaczyć Twoje statystyki treningów.',
    name: 'isTrainingStatisticsPublic',
  },
];
