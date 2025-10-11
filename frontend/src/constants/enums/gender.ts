export const Gender = {
  none: 'Nie wybrano',
  female: 'Kobieta',
  male: 'Mężczyzna',
} as const;

export type Gender = (typeof Gender)[keyof typeof Gender];
