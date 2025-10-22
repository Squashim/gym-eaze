export enum ReportType {
  inappropriateContent = 'Nieodpowiednia treść',
  spamOrAd = 'Spam lub reklama',
  misinformationOrDangerous = 'Dezinformacja lub niebezpieczne treści',
  privacyViolation = 'Naruszenie prywatności',
  copyrightViolation = 'Naruszenie praw autorskich',
  offTopicOrLowQuality = 'Treść nie na temat / niskiej jakości',
  userBehavior = 'Nieodpowiednie zachowanie użytkownika',
  other = 'Inny powód',
}
export const reportTypeValues = Object.values(ReportType);
