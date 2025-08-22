import type { LucideIcon } from 'lucide-react';
import type { AuthAccess } from './auth';
import type { UserRole } from './user';

export type MenuItem = {
  title: string;
  description?: string;
  url: string;
  allow?: AuthAccess;
  role?: UserRole;
  icon?: LucideIcon;
  items?: MenuItem[];
};
