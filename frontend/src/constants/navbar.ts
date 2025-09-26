import type { MenuItem } from '@/types/navbar';
import {
  CircleUser,
  ClipboardList,
  Dumbbell,
  Heart,
  HelpCircle,
  PlusCircle,
  Settings,
  User,
  UserPen,
} from 'lucide-react';

export const links: MenuItem[] = [
  {
    title: 'Plany treningowe',
    url: '/workouts',
    items: [
      {
        title: 'Przeglądaj',
        description: 'Odkryj dostępne plany treningowe',
        icon: ClipboardList,
        url: '/workouts/browse',
        allow: 'all',
      },
      {
        title: 'Moje plany',
        description: 'Twoje stworzone i aktywne plany treningowe',
        icon: User,
        url: '/workouts/my',
        allow: 'logged-in',
      },
      {
        title: 'Ulubione',
        description: 'Plany oznaczone jako ulubione',
        icon: Heart,
        url: '/workouts/favorites',
        allow: 'logged-in',
      },
      {
        title: 'Stwórz plan',
        description: 'Zaprojektuj własny plan treningowy w kreatorze',
        icon: PlusCircle,
        url: '/workouts/create',
        allow: 'logged-in',
      },
    ],
  },
  {
    title: 'Ćwiczenia',
    url: '/exercises',
    items: [
      {
        title: 'Przeglądaj',
        description: 'Znajdź różne ćwiczenia do swojego treningu',
        icon: Dumbbell,
        url: '/exercises/browse',
        allow: 'all',
      },
      {
        title: 'Moje ćwiczenia',
        description: 'Twoje własne i zapisane ćwiczenia',
        icon: User,
        url: '/exercises/my',
        allow: 'logged-in',
      },
      {
        title: 'Ulubione',
        description: 'Ćwiczenia oznaczone jako ulubione',
        icon: Heart,
        url: '/exercises/favorites',
        allow: 'logged-in',
      },
      {
        title: 'Dodaj ćwiczenie',
        description: 'Stwórz nowe ćwiczenie w kreatorze',
        icon: PlusCircle,
        url: '/exercises/create',
        allow: 'logged-in',
      },
    ],
  },
  {
    title: 'Przeglądaj posty',
    url: '/community',
    allow: 'all',
  },
  {
    title: 'Panel zarządzania',
    url: '/dashboard',
    allow: 'logged-in',
    role: 'admin',
  },
];

export const authorizedLinks: MenuItem[] = [
  {
    title: 'Mój profil',
    icon: CircleUser,
    url: '/profile/:username',
  },
  {
    title: 'Zarządzaj profilem',
    icon: UserPen,
    url: '/profile/edit',
  },
  {
    title: 'Ustawienia prywatności',
    icon: Settings,
    url: '/profile/privacy',
  },
  {
    title: 'Pomoc',
    icon: HelpCircle,
    url: '/help',
  },
];
