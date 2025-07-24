// mozna jako enum
const userRoles = ["user", "admin"] as const;
export type UserRole = (typeof userRoles)[number];
export default userRoles;
