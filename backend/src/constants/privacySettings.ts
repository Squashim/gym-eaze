// mozna jako enum
const privacySettings = ["Publiczny", "Prywatny"] as const;
export type PrivacySettings = (typeof privacySettings)[number];
export default privacySettings;
