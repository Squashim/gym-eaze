export enum StatusTraining {
  active = 'Aktywny',
  finished = 'Ukończony',
  canceled = 'Anulowany',
}
export const statusesValues = Object.values(StatusTraining);

export enum StatusReport {
  pending = 'Oczekujący',
  dismissed = 'Odrzucony',
  resolved = 'Rozwiązany',
}

export const statusReportValues = Object.values(StatusReport);
