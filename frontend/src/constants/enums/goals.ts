export const Goal = {
  none: 'Nie wybrano',
  recovery: 'Regeneracja i odpoczynek',
  strength: 'Zwiększanie siły',
  posture: 'Poprawa postawy ciała',
  massGain: 'Zwiększenie masy ciała',
  coordination: 'Poprawa koordynacji',
  calorieBurn: 'Spalanie kalorii',
  mobility: 'Poprawa mobilności',
  legsGlutes: 'Rozwój nóg i pośladków',
  endurance: 'Poprawa wytrzymałości',
  fatLoss: 'Redukcja tkanki tłuszczowej',
  muscleGain: 'Budowanie masy mięśniowej',
  flexibility: 'Poprawa gibkości',
} as const;

export type Goal = (typeof Goal)[keyof typeof Goal];
