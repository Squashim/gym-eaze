export const WorkoutStatus = {
  active: 'Aktywny',
  finished: 'Ukończony',
  canceled: 'Anulowany',
};
export type WorkoutStatus = (typeof WorkoutStatus)[keyof typeof WorkoutStatus];
