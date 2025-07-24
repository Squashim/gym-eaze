// mozna jako enum
const goals = [
  "Nie wybrano",
  "Regeneracja i odpoczynek",
  "Zwiększanie siły",
  "Poprawa postawy ciała",
  "Zwiększenie masy ciała",
  "Poprawa koordynacji",
  "Spalanie kalorii",
  "Poprawa mobilności",
  "Rozwój nóg i pośladków",
  "Poprawa wytrzymałości",
  "Redukcja tkanki tłuszczowej",
  "Budowanie masy mięśniowej",
  "Poprawa gibkości",
] as const;
export type Goals = (typeof goals)[number];
export default goals;
