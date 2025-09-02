export const parseField = (field?: string | string[]): string[] => {
  if (!field) return [];
  if (Array.isArray(field)) return field;
  try {
    return JSON.parse(field);
  } catch {
    return [field];
  }
};

export const parseDescription = (description: string): string => {
  const normalized = description.replace(/\r\n/g, '\n');
  return normalized;
};
