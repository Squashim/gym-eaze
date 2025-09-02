export const AdvancedLevel = {
  none: 'Nie wybrano',
  easy: 'Początkujący',
  medium: 'Średniozaawansowany',
  hard: 'Zaawansowany',
} as const;

export type AdvancedLevel = (typeof AdvancedLevel)[keyof typeof AdvancedLevel];
