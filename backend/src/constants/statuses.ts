// mozna jako enum i w nazwie dodałbym, że chodzi o statusy 'planu treningwego'
const statuses = ["Aktywny", "Ukończony", "Anulowany"] as const;
export type Status = (typeof statuses)[number];
export default statuses;

export enum stats {
  active = "Aktywny",
  finished = "Ukończony",
  canceled = "Anulowany",
}
