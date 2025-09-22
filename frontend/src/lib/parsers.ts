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

  if (lastTwoDigits >= 12 && lastTwoDigits <= 14) {
    return `${count} ${forms[2]}`;
  }

  if (lastDigit === 1) return `${count} ${forms[0]}`;
  if (lastDigit >= 2 && lastDigit <= 4) return `${count} ${forms[1]}`;

  return `${count} ${forms[2]}`;
}

export function parseWorkoutSetsLabel(count: number) {
  return parsePlurals(count, ['seria', 'serie', 'serii']);
}

export function parseExerciseLabel(count: number) {
  return parsePlurals(count, ['ćwiczenie', 'ćwiczenia', 'ćwiczeń']);
}

export function parseDayLabel(count: number) {
  if (count === 1) return `${count} dzień`;
  if (count >= 2) return `${count} dni`;
  return `${count} dni`;
}
