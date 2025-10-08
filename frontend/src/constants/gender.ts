export const Gender = {
  none: 'Nie wybrano',
  female: 'Kobieta',
  male: 'Mężczyzna',
};

export type Gender = (typeof Gender)[keyof typeof Gender];
