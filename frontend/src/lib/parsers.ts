export function parseSecondsToTimeString(totalSeconds: number): string {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  const parts: string[] = [];
  if (hours > 0) parts.push(`${hours} ${hours === 1 ? 'godz.' : 'godz.'}`);
  if (minutes > 0) parts.push(`${minutes} ${minutes === 1 ? 'min' : 'min'}`);
  if (seconds > 0 || parts.length === 0) parts.push(`${seconds} ${seconds === 1 ? 'sek' : 'sek'}`);
  return parts.join(' ');
}

export function parseTimeBrackets(seconds: number): string {
  const totalMinutes = Math.round(seconds / 60);

  if (totalMinutes < 10) return '5-10 minut';
  if (totalMinutes < 20) return '10-20 minut';
  if (totalMinutes < 40) return '20-40 minut';
  if (totalMinutes < 60) return '40-60 minut';
  if (totalMinutes < 90) return '60-90 minut';
  if (totalMinutes < 120) return '90-120 minut';
  if (totalMinutes < 180) return '120-180 minut';
  return '180 minut lub więcej';
}

function parsePlurals(count: number, forms: [string, string, string]): string {
  const lastDigit = count % 10;
  const lastTwoDigits = count % 100;

  if (count === 1) return `${count} ${forms[0]}`;
  if (lastDigit >= 2 && lastDigit <= 4 && !(lastTwoDigits >= 12 && lastTwoDigits <= 14)) {
    return `${count} ${forms[1]}`;
  }
  return `${count} ${forms[2]}`;
}

export function parseWorkoutSetsLabel(count: number) {
  return parsePlurals(count, ['seria', 'serie', 'serii']);
}

export function parseRepsLabel(count: number) {
  return parsePlurals(count, ['powtórzenie', 'powtórzenia', 'powtórzeń']);
}

export function parseExerciseLabel(count: number) {
  return parsePlurals(count, ['ćwiczenie', 'ćwiczenia', 'ćwiczeń']);
}

export function parseWorkoutLabel(count: number) {
  return parsePlurals(count, ['plan', 'plany', 'planów']);
}

export function parseAgeLabel(count: number) {
  return parsePlurals(count, ['rok', 'lata', 'lat']);
}

export function parsePersonReportedLabel(count: number) {
  return parsePlurals(count, ['osoba zgłosiła', 'osoby zgłosiły', 'osób zgłosiło']);
}

export function parseNotificationsLabel(count: number) {
  return parsePlurals(count, ['nowe powiadomienie', 'nowe powiadomienia', 'nowych powiadomień']);
}

export function parseWorkoutDurationLabel(days: number) {
  if (days === 7) return 'Tygodniowy';
  if (days === 14) return 'Dwutygodniowy';
  if (days === 21) return 'Trzytygodniowy';
  if (days === 30) return 'Miesięczny';
  return `${days}-dniowy`;
}

export function parseWorkoutDaysLabel(days: number, type: 'rest' | 'training') {
  if (type === 'rest') {
    return parsePlurals(days, ['dzień wolny', 'dni wolne', 'dni wolnych']);
  } else {
    return parsePlurals(days, ['dzień treningowy', 'dni treningowe', 'dni treningowych']);
  }
}

export function parseDayLabel(count: number) {
  if (count === 1) return `${count} dzień`;
  if (count >= 2) return `${count} dni`;
  return `${count} dni`;
}

export function parseTimeAgo(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();

  const seconds = Math.floor(diffMs / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) return `${parseDayLabel(days)} temu`;
  if (hours > 0) return `${parsePlurals(hours, ['godzinę', 'godziny', 'godzin'])} temu`;
  if (minutes > 0) return `${parsePlurals(minutes, ['minutę', 'minuty', 'minut'])} temu`;
  return `przed chwilą`;
}
