export const ReportType = {
  inappropriateContent: 'Nieodpowiednia treść',
  spamOrAd: 'Spam lub reklama',
  misinformationOrDangerous: 'Dezinformacja lub niebezpieczne treści',
  privacyViolation: 'Naruszenie prywatności',
  copyrightViolation: 'Naruszenie praw autorskich',
  offTopicOrLowQuality: 'Treść nie na temat / niskiej jakości',
  userBehavior: 'Nieodpowiednie zachowanie użytkownika',
  other: 'Inny powód',
} as const;

export type ReportType = (typeof ReportType)[keyof typeof ReportType];

export const ReportStatus = {
  pending: 'Oczekujący',
  dismissed: 'Odrzucony',
  resolved: 'Rozwiązany',
} as const;

export type StatusReport = (typeof ReportStatus)[keyof typeof ReportStatus];
