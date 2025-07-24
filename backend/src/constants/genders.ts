// mozna jako enum
const gender = ["Nie wybrano", "Kobieta", "Mężczyzna"] as const;
export type Gender = (typeof gender)[number];
export default gender;
