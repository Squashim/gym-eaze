// mozna jako enum
const advancedLevel = [
  "Początkujący",
  "Średniozaawansowany",
  "Zaawansowany",
] as const;
export type AdvancedLevel = (typeof advancedLevel)[number];
export default advancedLevel;
