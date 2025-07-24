// mozna jako enum
const notifications = ["Informacja", "Ostrzeżenie", "Błąd"] as const;
export type Notifications = (typeof notifications)[number];
export default notifications;
