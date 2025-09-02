export const parseField = (field?: string | string[]): string[] => {
  if (!field) return [];
  if (Array.isArray(field)) return field;
  try {
    return JSON.parse(field);
  } catch {
    return [field];
  }
};
